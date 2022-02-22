package ver12.view.Dialog.Dialogs.GameSelection.Cards;

import ver12.SharedClasses.GameSettings;
import ver12.SharedClasses.Sync.SyncedListType;
import ver12.view.Dialog.Components.Parent;
import ver12.view.Dialog.Dialogs.BackOkInterface;
import ver12.view.Dialog.Dialogs.GameSelection.Components.SyncedGamesList;
import ver12.view.Dialog.Dialogs.Header;

public class ResumeUnfinishedGame extends SyncedGamesList implements BackOkInterface {

    public ResumeUnfinishedGame(Parent parent, GameSettings gameSettings) {
        super(new Header("Resume Unfinished Game vs Ai"), SyncedListType.RESUMABLE_GAMES, parent, gameSettings, GameSettings.GameType.RESUME);
    }


}
