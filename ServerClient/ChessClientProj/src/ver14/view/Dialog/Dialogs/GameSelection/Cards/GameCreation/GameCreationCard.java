package ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.WinPnl;

import java.awt.*;

public abstract class GameCreationCard extends DialogCard {
    protected final GameSettings gameSettings;
    protected final Checkbox checkbox;


    public GameCreationCard(CardHeader cardHeader, Dialog parentDialog, GameSettings gameSettings) {
        super(cardHeader, parentDialog);
        this.gameSettings = gameSettings;
        checkbox = new Checkbox("Uncheck To Discard");
        checkbox.addItemListener(l -> {
            changeState(checkbox.getState());//no infinite loop. item event is only called after a user click, and not setState
        });
        changeState(false);
    }

    protected abstract void changeState(boolean state);

    @Override
    public WinPnl createNavPnl() {
        WinPnl pnl = super.createNavPnl();
        pnl.add(checkbox);
        return pnl;
    }

    @Override
    protected int navCols() {
        return WinPnl.ALL_IN_ONE_ROW;
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
