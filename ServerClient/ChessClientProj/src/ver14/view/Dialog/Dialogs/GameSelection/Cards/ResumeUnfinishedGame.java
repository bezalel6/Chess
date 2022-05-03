package ver14.view.Dialog.Dialogs.GameSelection.Cards;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.view.Dialog.BackOk.BackOkInterface;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.GameSelection.Components.SyncedGamesList;
import ver14.view.Dialog.Dialogs.Header;

/**
 * Resume unfinished game - a list of all unfinished games this player can resume.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ResumeUnfinishedGame extends SyncedGamesList implements BackOkInterface {

    /**
     * Instantiates a new Resume unfinished game.
     *
     * @param parent       the parent
     * @param gameSettings the game settings
     */
    public ResumeUnfinishedGame(Parent parent, GameSettings gameSettings) {
        super(new Header("Resume Unfinished Game vs Ai"), SyncedListType.RESUMABLE_GAMES, parent, gameSettings, GameSettings.GameType.RESUME);
    }

    @Override
    public String getOkText() {
        return "OK";
    }
}
