package ver12.view.Dialog.Cards;

import ver12.SharedClasses.Callbacks.MessageCallback;
import ver12.SharedClasses.FontManager;
import ver12.SharedClasses.IDsGenerator;
import ver12.SharedClasses.messages.Message;
import ver12.SharedClasses.ui.MyJButton;
import ver12.view.Dialog.Components.Child;
import ver12.view.Dialog.Components.DialogComponent;
import ver12.view.Dialog.Components.Parent;
import ver12.view.Dialog.Dialog;
import ver12.view.Dialog.Dialogs.BackOkInterface;
import ver12.view.Dialog.Dialogs.BackOkPnl;
import ver12.view.Dialog.SyncableList;
import ver12.view.Dialog.Verified;
import ver12.view.Dialog.WinPnl;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.util.ArrayList;


public abstract class DialogCard extends WinPnl implements BackOkInterface, Child, Parent, AncestorListener {
    private final static IDsGenerator cardIDs = new IDsGenerator();
    private static final boolean WIREFRAME = false;
    protected final Dialog parentDialog;
    private final CardHeader cardHeader;
    private final ArrayList<Verified> verifiedComponentsList;
    private final MyJButton navBtn;
    private final String cardID;
    private BackOkPnl backOkPnl;

    public DialogCard(CardHeader cardHeader, Dialog parentDialog) {
        this(cardHeader, parentDialog, null);
        this.backOkPnl = new BackOkPnl(this);
        this.bottomPnl.add(backOkPnl);
    }

    public DialogCard(CardHeader cardHeader, Dialog parentDialog, BackOkInterface backOk) {
        super(cardHeader);
        this.cardHeader = cardHeader;
        this.verifiedComponentsList = new ArrayList<>();
        this.parentDialog = parentDialog;
        this.cardID = cardIDs.generate();
        if (backOk != null) {
            this.backOkPnl = new BackOkPnl(backOk);
            this.bottomPnl.add(backOkPnl);
        }
        addAncestorListener(this);
        navBtn = new MyJButton(getCardName(), FontManager.normal, this::navToMe);
    }

    public String getCardName() {
        return cardHeader.getCardName();
    }

//    @Override
//    public Dimension getPreferredSize() {
//        return Size.max(super.getPreferredSize());
//    }

    public void navToMe() {
        parentDialog.switchTo(this);
    }

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
        parentDialog.onUpdate();
    }

    @Override
    public void addToNavText(String str) {
        navBtn.setText(getCardName() + str);
    }

    @Override
    public void done() {
        parentDialog.done();
    }

    @Override
    public void back() {
        parentDialog.back();
    }

    public void addNavigationTo(DialogCard card) {
        parentDialog.addCard(card);
        add(card.navToMePnl());
    }

    @Override

    public Component add(Component comp) {
        if (comp instanceof Verified verified) {
            verifiedComponentsList.add(verified);
        }
        if (WIREFRAME && comp instanceof JComponent jc) {
            jc.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
        }

        return super.add(comp);
    }

    public JPanel navToMePnl() {
        JPanel ret = new JPanel();
        ret.add(navBtn);
        return ret;
    }

    public boolean dialogWideErrors() {
        return false;
    }

    public void addDialogComponent(DialogComponent component) {
        add(component);
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
        if (backOkPnl != null)
            backOkPnl.enableOk(ret == null);
        return ret;
    }

    @Override
    public String toString() {
        return cardHeader.getCardName();
    }

    @Override
    public void onBack() {
        parentDialog.popCard();
    }

    /**
     * closes dialog
     */
    @Override
    public void onOk() {
        parentDialog.closeDialog();
    }
}