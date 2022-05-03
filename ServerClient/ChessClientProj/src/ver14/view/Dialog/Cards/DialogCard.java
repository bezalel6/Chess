package ver14.view.Dialog.Cards;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Misc.IDsGenerator;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.SharedClasses.UI.Buttons.MyJButton;
import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.BackOk.BackOkContainer;
import ver14.view.Dialog.BackOk.BackOkInterface;
import ver14.view.Dialog.BackOk.BackOkPnl;
import ver14.view.Dialog.Components.Child;
import ver14.view.Dialog.Components.DialogComponent;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Components.Verified;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.SyncableList;
import ver14.view.Dialog.WinPnl;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.util.ArrayList;


/**
 * represents a Dialog card. a panel for displaying and managing {@link DialogComponent}s.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class DialogCard extends WinPnl implements Child, Parent, AncestorListener, BackOkContainer {
    /**
     * The constant cardIDs.
     */
    private final static IDsGenerator cardIDs = new IDsGenerator();
    /**
     * The constant navigationBtnsFont.
     */
    private static final Font navigationBtnsFont = FontManager.normal;
    /**
     * The Parent dialog.
     */
    protected final Dialog parentDialog;
    /**
     * The Card header.
     */
    private final CardHeader cardHeader;
    /**
     * The Verified components list.
     */
    private final ArrayList<Verified> verifiedComponentsList;
    /**
     * The Nav btn.
     */
    private final MyJButton navBtn;
    /**
     * The Card id.
     */
    private final String cardID;
    /**
     * The Default value btns.
     */
    private final ArrayList<MyJButton> defaultValueBtns;
    /**
     * The Advanced settings str.
     */
    private String advancedSettingsStr = "Advanced Settings";
    /**
     * The Optional header.
     */
//    private BackOkPnl backOkPnl;
    private Header optionalHeader = null;
    /**
     * The Back ok interface.
     */
    private BackOkInterface backOkInterface;
    /**
     * The Nav text.
     */
    private String navText = getCardName();
    /**
     * The Overrideable size.
     */
    private boolean overrideableSize = false;

    /**
     * Instantiates a new Dialog card.
     *
     * @param cardHeader   the card header
     * @param parentDialog the parent dialog
     */
    public DialogCard(CardHeader cardHeader, Dialog parentDialog) {
        this(cardHeader, parentDialog, null);
        setBackOk(defaultInterface());
    }

    /**
     * Instantiates a new Dialog card.
     *
     * @param cardHeader   the card header
     * @param parentDialog the parent dialog
     * @param backOk       the back ok
     */
    public DialogCard(CardHeader cardHeader, Dialog parentDialog, BackOkInterface backOk) {
        super(1, cardHeader);
        this.cardHeader = cardHeader;
        navText = getCardName();
        this.verifiedComponentsList = new ArrayList<>();
        this.defaultValueBtns = new ArrayList<>();
        this.parentDialog = parentDialog;
        this.cardID = cardIDs.generate();
        setBackOk(backOk);
        addAncestorListener(this);
        navBtn = new MyJButton(navText, navigationBtnsFont, this::navToMe);
        checkVerifiedComponents();
    }

    /**
     * Sets the back ok interface.
     *
     * @param backOk the back ok
     */
    public void setBackOk(BackOkInterface backOk) {
        backOk = backOk == null ? BackOkInterface.noInterface : backOk;
        backOkInterface = backOk;
    }

    /**
     * create the default dialog card back ok interface. which is going back to the previous card on clicking "back" and closing the dialog when clicking "ok".
     *
     * @return the back ok interface
     */
    protected BackOkInterface defaultInterface() {
        return new BackOkInterface() {
            @Override
            public void onBack() {
                parentDialog.popCard();
            }

            @Override
            public void onOk() {
                if (parentDialog != null)
                    parentDialog.closeDialog();
            }
        };
    }

    /**
     * Gets card name.
     *
     * @return the card name
     */
    public String getCardName() {
        if (cardHeader != null)
            return cardHeader.getCardName();
        return "";

    }

    /**
     * Navigate to this dialog card.
     */
    public void navToMe() {
        parentDialog.switchTo(this);
    }

    /**
     * Check verified components.
     *
     * @return Error message if any not verified, null otherwise
     */
    public String checkVerifiedComponents() {
        String ret = null;
        for (Verified comp : verifiedComponentsList) {
            //keep verifying all components, but set the global error to the first one found
            if (!comp.verify() && ret == null) {
                ret = comp.errorDetails();
//                break;
            }
        }
        if (backOkPnl() != null)
            backOkPnl().enableOk(ret == null);
        return ret;
    }

    /**
     * Sets overrideable size.
     */
    public void setOverrideableSize() {
        setOverrideableSize(true);
    }

    /**
     * Is overrideable size boolean.
     *
     * @return the boolean
     */
    public boolean isOverrideableSize() {
        return overrideableSize;
    }

    /**
     * Sets overrideable size.
     *
     * @param overrideableSize the overrideable size
     */
    public void setOverrideableSize(boolean overrideableSize) {
        this.overrideableSize = overrideableSize;
    }

    @Override
    public BackOkInterface actualInterface() {
        return backOkInterface;
    }

    /**
     * called when this card gets Displayed.
     */
    public void displayed() {
        SwingUtilities.invokeLater(() -> {
            onUpdate();
            scrollToTop();
        });
    }

    /**
     * Gets card id.
     *
     * @return the card id
     */
    public String getCardID() {
        return cardID;
    }

    @Override
    public void ancestorAdded(AncestorEvent event) {
        onUpdate();
    }

    @Override
    public void ancestorRemoved(AncestorEvent event) {
    }

    @Override
    public void ancestorMoved(AncestorEvent event) {
    }

    @Override
    public Parent parent() {
        return parentDialog;
    }

    @Override
    public void registerSyncedList(SyncableList list) {
        parentDialog.registerSyncedList(list);
    }

    @Override
    public void askServer(Message msg, MessageCallback onRes) {
        parentDialog.askServer(msg, onRes);
    }

    @Override
    public void onUpdate() {
        if (parentDialog != null)
            parentDialog.onUpdate();
    }

    @Override
    public void addToNavText(String str) {
        setNavText(getCardName() + str);
    }

    /**
     * Sets navigation text.
     *
     * @param s the s
     */
    private void setNavText(String s) {
        s = StrUtils.fixHtml(s);
        navText = s;
        if (optionalHeader == null) {
            navBtn.setText(s);
        } else {
            optionalHeader.setText(s);
        }
    }

    @Override
    public void done() {
        parentDialog.done();
    }

    @Override
    public void back() {
        parentDialog.back();
    }

    @Override
    public DialogCard currentCard() {
        return this;
    }

    @Override
    public BackOkPnl backOkPnl() {
        return parentDialog.backOkPnl();
    }

    @Override
    public void addOnClose(VoidCallback callback) {
        parentDialog.addOnClose(callback);
    }

    @Override
    public void enableNavBtn(boolean b) {
        Parent.super.enableNavBtn(b);
        navBtn.setEnabled(b);
    }

    /**
     * Add navigation to.
     *
     * @param card the card
     */
    public void addNavigationTo(DialogCard card) {
        parentDialog.addCard(card);
        add(card.createNavPnl());
    }

    @Override

    public Component add(Component comp) {
        if (comp instanceof Verified verified) {
            verifiedComponentsList.add(verified);
        }
        return super.add(comp);

    }

    /**
     * Create nav pnl win pnl.
     *
     * @return the win pnl
     */
    public WinPnl createNavPnl() {
        WinPnl ret = new WinPnl(navCols());
        if (!defaultValueBtns.isEmpty()) {
            optionalHeader = new Header(navText);
            ret.setHeader(optionalHeader);

            defaultValueBtns.forEach(ret::add);
            navBtn.setText(advancedSettingsStr);
            ret.add(navBtn);

            ret.setBorder();
        } else {
            ret.add(navBtn);
        }
        return ret;
    }

    /**
     * Nav cols int.
     *
     * @return the int
     */
    protected int navCols() {
        return 1;
    }

    /**
     * Sets advanced settings str.
     *
     * @param advancedSettingsStr the advanced settings str
     */
    public void setAdvancedSettingsStr(String advancedSettingsStr) {
        this.advancedSettingsStr = advancedSettingsStr;
    }

    /**
     * Add default value btn.
     *
     * @param txt     the txt
     * @param onClick the on click
     */
    public void addDefaultValueBtn(String txt, VoidCallback onClick) {
        this.defaultValueBtns.add(new MyJButton(txt, navigationBtnsFont, onClick));
    }

    /**
     * should error on this card be treated as Dialog wide errors.
     *
     * @return the boolean
     */
    public boolean dialogWideErrors() {
        return false;
    }

    /**
     * Add dialog component.
     *
     * @param component the component
     */
    public void addDialogComponent(DialogComponent component) {
        add(component);
    }

    @Override
    public String toString() {
        return cardHeader.getCardName();
    }


}
