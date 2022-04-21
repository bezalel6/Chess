package ver14.view.Dialog.Cards;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Misc.IDsGenerator;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.SharedClasses.UI.Buttons.MyJButton;
import ver14.SharedClasses.UI.FontManager;
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


public abstract class DialogCard extends WinPnl implements Child, Parent, AncestorListener, BackOkContainer {
    private final static IDsGenerator cardIDs = new IDsGenerator();
    private static final Font navigationBtnsFont = FontManager.normal;
    protected final Dialog parentDialog;
    private final CardHeader cardHeader;
    private final ArrayList<Verified> verifiedComponentsList;
    private final MyJButton navBtn;
    private final String cardID;
    private final ArrayList<MyJButton> defaultValueBtns;
    private String advancedSettingsStr = "Advanced Settings";
    //    private BackOkPnl backOkPnl;
    private Header optionalHeader = null;
    private BackOkInterface backOkInterface;

    public DialogCard(CardHeader cardHeader, Dialog parentDialog) {
        this(cardHeader, parentDialog, null);
        setBackOk(defaultInterface());
    }

    public DialogCard(CardHeader cardHeader, Dialog parentDialog, BackOkInterface backOk) {
        super(1, cardHeader);
        this.cardHeader = cardHeader;
        this.verifiedComponentsList = new ArrayList<>();
        this.defaultValueBtns = new ArrayList<>();
        this.parentDialog = parentDialog;
        this.cardID = cardIDs.generate();
        setBackOk(backOk);
        addAncestorListener(this);
        navBtn = new MyJButton(getCardName(), navigationBtnsFont, this::navToMe);
        checkVerifiedComponents();
    }

    public void setBackOk(BackOkInterface backOk) {
        backOk = backOk == null ? BackOkInterface.noInterface : backOk;
        backOkInterface = backOk;
//        if (backOk != null) {
//            this.backOkPnl = new BackOkPnl(backOk);
//            this.bottomPnl.add(backOkPnl);
//            parentDialog.setFocusOn(backOkPnl.getOk());
//        }
    }

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

    public String getCardName() {
        return cardHeader.getCardName();
    }

    public void navToMe() {
        parentDialog.switchTo(this);
    }

    /**
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

    @Override
    public BackOkInterface actualInterface() {
        return backOkInterface;
    }

    public void shown() {
        SwingUtilities.invokeLater(() -> {
            onUpdate();
            scrollToTop();
            onUpdate();
        });
    }

//    @Override
//    public Dimension getPreferredSize() {
//        return Size.max(super.getPreferredSize());
//    }

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

    private void setNavText(String s) {
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

    public JPanel createNavPnl() {
        WinPnl ret = new WinPnl();
        if (!defaultValueBtns.isEmpty()) {
            optionalHeader = new Header(navBtn.getText());
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

    public void setAdvancedSettingsStr(String advancedSettingsStr) {
        this.advancedSettingsStr = advancedSettingsStr;
    }

    public void addDefaultValueBtn(String txt, VoidCallback onClick) {
        this.defaultValueBtns.add(new MyJButton(txt, navigationBtnsFont, onClick));
    }

    public boolean dialogWideErrors() {
        return false;
    }

    public void addDialogComponent(DialogComponent component) {
        add(component);
    }

    @Override
    public String toString() {
        return cardHeader.getCardName();
    }


}
