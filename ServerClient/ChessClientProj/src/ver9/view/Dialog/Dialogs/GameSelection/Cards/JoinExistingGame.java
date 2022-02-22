package ver9.view.Dialog.Dialogs.GameSelection.Cards;

import ver9.SharedClasses.GameSettings;
import ver9.SharedClasses.Sync.SyncedListType;
import ver9.view.Dialog.Components.Parent;
import ver9.view.Dialog.Dialogs.GameSelection.Components.SyncedGamesList;
import ver9.view.Dialog.Dialogs.Header;

public class JoinExistingGame extends SyncedGamesList {

    public JoinExistingGame(Parent parent, GameSettings gameSettings) {
        super(new Header("Choose Game To Join"), SyncedListType.JOINABLE_GAMES, parent, gameSettings, GameSettings.GameType.JOIN_EXISTING);
    }
}
