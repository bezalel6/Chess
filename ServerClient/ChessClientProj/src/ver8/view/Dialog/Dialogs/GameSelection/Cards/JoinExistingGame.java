package ver8.view.Dialog.Dialogs.GameSelection.Cards;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.view.Dialog.Components.SyncableListComponent;
import ver8.view.Dialog.Dialog;
import ver8.view.OldDialogs.GameSelect.selectable.SelectableGame;
import ver8.view.OldDialogs.Header;
import ver8.view.OldDialogs.Navigation.BackOkInterface;

public class JoinExistingGame extends SyncableListComponent implements BackOkInterface {
    private final GameSettings gameSettings;

    public JoinExistingGame(Dialog parent, GameSettings gameSettings) {
        super(new Header("Choose Game To Join"), SyncedListType.JOINABLE_GAMES, parent);
        this.gameSettings = gameSettings;
    }

    @Override
    protected void onSelected() {
        String id = null;
        if (selected != null) {
            id = ((SelectableGame) selected).gameInfo.gameId;
        }
        gameSettings.setJoiningToGameID(id);
    }

    @Override
    public void onBack() {

    }

    @Override
    public void onOk() {
        gameSettings.setGameType(GameSettings.GameType.JOIN_EXISTING);
    }
}
