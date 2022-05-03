package ver14.view.Dialog.Dialogs.GameSelection;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Utils.ArrUtils;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.DialogProperties;
import ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation.GameCreation;
import ver14.view.Dialog.Dialogs.GameSelection.Cards.JoinExistingGame;
import ver14.view.Dialog.Dialogs.GameSelection.Cards.ResumeUnfinishedGame;
import ver14.view.IconManager.Size;

/**
 * represents a Game selection dialog, when the client can create a custom game, join an existing game, and resume an unfinished game.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class GameSelect extends Dialog {
    /**
     * The Game settings.
     */
    private final GameSettings gameSettings;


    /**
     * Instantiates a new Game select.
     *
     * @param dialogProperties the properties
     */
    public GameSelect(DialogProperties dialogProperties) {
        super(dialogProperties);
        gameSettings = new GameSettings();
        DialogCard[] cards = new DialogCard[]{
                new GameCreation(this, gameSettings),
                new JoinExistingGame(this, gameSettings).createCard(),
        };
        if (!dialogProperties.getLoginInfo().isGuest()) {
            cards = ArrUtils.concat(cards, new ResumeUnfinishedGame(this, gameSettings).createCard());
        }

        navigationCardSetup(new Size(400, 500), cards);
    }


    /**
     * Gets game settings.
     *
     * @return the game settings
     */
    public GameSettings getGameSettings() {
        return gameSettings;
    }
}
