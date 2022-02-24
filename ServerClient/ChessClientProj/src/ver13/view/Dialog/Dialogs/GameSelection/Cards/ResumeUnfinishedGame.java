package ver13.view.Dialog.Dialogs.GameSelection.Cards;

import ver13.SharedClasses.GameSettings;
import ver13.SharedClasses.Sync.SyncedListType;
import ver13.view.Dialog.Components.Parent;
import ver13.view.Dialog.Dialogs.BackOkInterface;
import ver13.view.Dialog.Dialogs.GameSelection.Components.SyncedGamesList;
import ver13.view.Dialog.Dialogs.Header;

public class ResumeUnfinishedGame extends SyncedGamesList implements BackOkInterface {

    public ResumeUnfinishedGame(Parent parent, GameSettings gameSettings) {
        super(new Header("Resume Unfinished Game vs Ai"), SyncedListType.RESUMABLE_GAMES, parent, gameSettings, GameSettings.GameType.RESUME);
    }


}
