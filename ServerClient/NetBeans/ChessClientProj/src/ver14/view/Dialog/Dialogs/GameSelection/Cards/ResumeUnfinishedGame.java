package ver14.view.Dialog.Dialogs.GameSelection.Cards;

import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.BackOkInterface;
import ver14.view.Dialog.Dialogs.GameSelection.Components.SyncedGamesList;
import ver14.view.Dialog.Dialogs.Header;

public class ResumeUnfinishedGame extends SyncedGamesList implements BackOkInterface {

    public ResumeUnfinishedGame(Parent parent, GameSettings gameSettings) {
        super(new Header("Resume Unfinished Game vs Ai"), SyncedListType.RESUMABLE_GAMES, parent, gameSettings, GameSettings.GameType.RESUME);
    }


}