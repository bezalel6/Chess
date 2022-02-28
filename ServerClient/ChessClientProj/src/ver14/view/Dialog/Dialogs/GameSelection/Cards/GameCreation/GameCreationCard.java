package ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver14.SharedClasses.GameSettings;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;

import javax.swing.*;
import java.awt.*;

public abstract class GameCreationCard extends DialogCard {
    protected final GameSettings gameSettings;
    protected final Checkbox checkbox;


    public GameCreationCard(CardHeader cardHeader, Dialog parentDialog, GameSettings gameSettings) {
        super(cardHeader, parentDialog);
        this.gameSettings = gameSettings;
        checkbox = new Checkbox();
        checkbox.addItemListener(l -> {
            changeState(checkbox.getState());//no infinite loop. item event is only called after a user click, and not setState
        });
        changeState(false);
    }

    protected abstract void changeState(boolean state);

    @Override
    public JPanel navToMePnl() {
        JPanel pnl = super.navToMePnl();
        pnl.add(checkbox);
        return pnl;
    }

    @Override
    public void onBack() {
        changeState(false);
        super.onBack();
    }

    @Override
    public void onOk() {
        changeState(true);
        super.onBack();
    }
}
