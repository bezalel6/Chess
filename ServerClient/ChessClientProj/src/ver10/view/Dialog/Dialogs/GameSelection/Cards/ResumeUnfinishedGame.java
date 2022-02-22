package ver10.view.Dialog.Dialogs.GameSelection.Cards;

import ver10.SharedClasses.GameSettings;
import ver10.SharedClasses.Sync.SyncedListType;
import ver10.view.Dialog.Components.Parent;
import ver10.view.Dialog.Dialogs.BackOkInterface;
import ver10.view.Dialog.Dialogs.GameSelection.Components.SyncedGamesList;
import ver10.view.Dialog.Dialogs.Header;

public class ResumeUnfinishedGame extends SyncedGamesList implements BackOkInterface {

    public ResumeUnfinishedGame(Parent parent, GameSettings gameSettings) {
        super(new Header("Resume Unfinished Game vs Ai"), SyncedListType.RESUMABLE_GAMES, parent, gameSettings, GameSettings.GameType.RESUME);
    }


}
