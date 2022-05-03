package ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.WinPnl;

import java.awt.*;

/**
 * represents a Game creation card.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class GameCreationCard extends DialogCard {
    /**
     * The Game settings.
     */
    protected final GameSettings gameSettings;
    /**
     * The Checkbox.
     */
    protected final Checkbox checkbox;


    /**
     * Instantiates a new Game creation card.
     *
     * @param cardHeader   the card header
     * @param parentDialog the parent dialog
     * @param gameSettings the game settings
     */
    public GameCreationCard(CardHeader cardHeader, Dialog parentDialog, GameSettings gameSettings) {
        super(cardHeader, parentDialog);
        this.gameSettings = gameSettings;
        checkbox = new Checkbox("Uncheck To Discard");
        checkbox.addItemListener(l -> {
            changeState(checkbox.getState());//no infinite loop. item event is only called after a user click, and not setState
        });
        changeState(false);
    }

    /**
     * Change state.
     *
     * @param state the state
     */
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
