package ver14.view.Dialog.Dialogs.GameSelection.Cards;

import ver14.SharedClasses.GameSettings;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.GameSelection.Components.SyncedGamesList;
import ver14.view.Dialog.Dialogs.Header;

public class JoinExistingGame extends SyncedGamesList {

    public JoinExistingGame(Parent parent, GameSettings gameSettings) {
        super(new Header("Join Existing Game"), SyncedListType.JOINABLE_GAMES, parent, gameSettings, GameSettings.GameType.JOIN_EXISTING);
    }
}
