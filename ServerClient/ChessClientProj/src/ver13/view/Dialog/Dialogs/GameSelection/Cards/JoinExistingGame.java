package ver13.view.Dialog.Dialogs.GameSelection.Cards;

import ver13.SharedClasses.GameSettings;
import ver13.SharedClasses.Sync.SyncedListType;
import ver13.view.Dialog.Components.Parent;
import ver13.view.Dialog.Dialogs.GameSelection.Components.SyncedGamesList;
import ver13.view.Dialog.Dialogs.Header;

public class JoinExistingGame extends SyncedGamesList {

    public JoinExistingGame(Parent parent, GameSettings gameSettings) {
        super(new Header("Join Existing Game"), SyncedListType.JOINABLE_GAMES, parent, gameSettings, GameSettings.GameType.JOIN_EXISTING);
    }
}
