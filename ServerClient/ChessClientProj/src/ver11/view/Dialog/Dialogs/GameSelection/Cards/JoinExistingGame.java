package ver11.view.Dialog.Dialogs.GameSelection.Cards;

import ver11.SharedClasses.GameSettings;
import ver11.SharedClasses.Sync.SyncedListType;
import ver11.view.Dialog.Components.Parent;
import ver11.view.Dialog.Dialogs.GameSelection.Components.SyncedGamesList;
import ver11.view.Dialog.Dialogs.Header;

public class JoinExistingGame extends SyncedGamesList {

    public JoinExistingGame(Parent parent, GameSettings gameSettings) {
        super(new Header("Join Existing Game"), SyncedListType.JOINABLE_GAMES, parent, gameSettings, GameSettings.GameType.JOIN_EXISTING);
    }
}
