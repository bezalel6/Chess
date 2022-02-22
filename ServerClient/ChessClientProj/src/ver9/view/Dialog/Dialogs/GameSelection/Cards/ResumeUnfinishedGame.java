package ver9.view.Dialog.Dialogs.GameSelection.Cards;

import ver9.SharedClasses.GameSettings;
import ver9.SharedClasses.Sync.SyncedListType;
import ver9.view.Dialog.Components.Parent;
import ver9.view.Dialog.Dialogs.BackOkInterface;
import ver9.view.Dialog.Dialogs.GameSelection.Components.SyncedGamesList;
import ver9.view.Dialog.Dialogs.Header;

public class ResumeUnfinishedGame extends SyncedGamesList implements BackOkInterface {

    public ResumeUnfinishedGame(Parent parent, GameSettings gameSettings) {
        super(new Header("Choose Game To Resume"), SyncedListType.RESUMABLE_GAMES, parent, gameSettings, GameSettings.GameType.RESUME);
    }


}
