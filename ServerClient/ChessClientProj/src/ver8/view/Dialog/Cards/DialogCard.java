package ver8.view.Dialog.Cards;

import ver8.SharedClasses.FontManager;
import ver8.SharedClasses.ui.MyJButton;
import ver8.view.Dialog.Components.DialogComponent;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Verified;
import ver8.view.Dialog.WinPnl;
import ver8.view.OldDialogs.Navigation.BackOkInterface;
import ver8.view.OldDialogs.Navigation.BackOkPnl;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public abstract class DialogCard extends WinPnl implements BackOkInterface {
    protected final ver8.view.Dialog.Dialog parentDialog;
    private final CardHeader cardHeader;
    private final ArrayList<Verified> verifiedComponentsList;
    private final BackOkPnl backOkPnl;

    public DialogCard(CardHeader cardHeader, ver8.view.Dialog.Dialog parentDialog) {
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
    }

    public void onInputUpdate() {

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
        ret.add(new MyJButton(getCardName(), FontManager.normal, this::navToMe));
        return ret;
    }

    public String getCardName() {
        return cardHeader.getCardName();
    }

    public void navToMe() {
        parentDialog.switchTo(this);
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
