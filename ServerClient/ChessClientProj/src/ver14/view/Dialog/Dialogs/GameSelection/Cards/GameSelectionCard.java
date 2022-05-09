package ver14.view.Dialog.Dialogs.GameSelection.Cards;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;

/**
 * a Game selection card.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class GameSelectionCard extends DialogCard {
    /**
     * The Game settings.
     */
    protected final GameSettings gameSettings;
    /**
     * The Game type.
     */
    protected final ver14.SharedClasses.Game.GameSetup.GameType gameType;

    /**
     * Instantiates a new Game selection card.
     *
     * @param cardHeader   the card header
     * @param parentDialog the parent dialog
     * @param gameSettings the game settings
     * @param gameType     the game type
     */
    public GameSelectionCard(CardHeader cardHeader, Dialog parentDialog, GameSettings gameSettings, ver14.SharedClasses.Game.GameSetup.GameType gameType) {
        super(cardHeader, parentDialog);
        this.gameSettings = gameSettings;
        this.gameType = gameType;
    }


    @Override
    public void onOk() {
        gameSettings.setGameType(gameType);
        super.onOk();
    }
}
