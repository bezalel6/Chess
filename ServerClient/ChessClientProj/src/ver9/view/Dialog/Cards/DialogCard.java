package ver9.view.Dialog.Cards;

import ver9.SharedClasses.Callbacks.MessageCallback;
import ver9.SharedClasses.FontManager;
import ver9.SharedClasses.messages.Message;
import ver9.SharedClasses.ui.MyJButton;
import ver9.view.Dialog.Components.Child;
import ver9.view.Dialog.Components.DialogComponent;
import ver9.view.Dialog.Components.Parent;
import ver9.view.Dialog.Dialog;
import ver9.view.Dialog.Dialogs.BackOkInterface;
import ver9.view.Dialog.Dialogs.BackOkPnl;
import ver9.view.Dialog.SyncableList;
import ver9.view.Dialog.Verified;
import ver9.view.Dialog.WinPnl;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public abstract class DialogCard extends WinPnl implements BackOkInterface, Child, Parent {
    protected final Dialog parentDialog;
    private final CardHeader cardHeader;
    private final ArrayList<Verified> verifiedComponentsList;
    private final BackOkPnl backOkPnl;
    private final MyJButton navBtn;

    public DialogCard(CardHeader cardHeader, Dialog parentDialog) {
        this(cardHeader, parentDialog, true);
    }

    public DialogCard(CardHeader cardHeader, Dialog parentDialog, boolean addBackOk) {
        super(cardHeader);
        this.cardHeader = cardHeader;
        this.verifiedComponentsList = new ArrayList<>();
        this.parentDialog = parentDialog;
        this.backOkPnl = new BackOkPnl(this);
        if (addBackOk) {
            this.bottomPnl.add(backOkPnl);
        }
        navBtn = new MyJButton(getCardName(), FontManager.normal, this::navToMe);
    }

    public String getCardName() {
        return cardHeader.getCardName();
    }

    public void navToMe() {
        parentDialog.switchTo(this);
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

    public void addNavigationTo(DialogCard card) {
        parentDialog.addCard(card);
        add(card.navToMePnl());
    }

    @Override
    public Component add(Component comp) {
        if (comp instanceof Verified verified) {
            verifiedComponentsList.add(verified);
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
