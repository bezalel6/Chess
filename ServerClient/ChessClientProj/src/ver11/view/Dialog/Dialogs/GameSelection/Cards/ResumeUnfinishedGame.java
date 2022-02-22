package ver11.view.Dialog.Dialogs.GameSelection.Cards;

import ver11.SharedClasses.GameSettings;
import ver11.SharedClasses.Sync.SyncedListType;
import ver11.view.Dialog.Components.Parent;
import ver11.view.Dialog.Dialogs.BackOkInterface;
import ver11.view.Dialog.Dialogs.GameSelection.Components.SyncedGamesList;
import ver11.view.Dialog.Dialogs.Header;

public class ResumeUnfinishedGame extends SyncedGamesList implements BackOkInterface {

    public ResumeUnfinishedGame(Parent parent, GameSettings gameSettings) {
        super(new Header("Resume Unfinished Game vs Ai"), SyncedListType.RESUMABLE_GAMES, parent, gameSettings, GameSettings.GameType.RESUME);
    }


}
