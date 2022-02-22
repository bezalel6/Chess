package ver8.view.Wishfull.dialogs.GameSelect.Panels;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.view.Wishfull.List.Synced.SyncedGames;
import ver8.view.Wishfull.dialogs.GameSelect.Selectables.SelectableGame;
import ver8.view.Wishfull.dialogs.DialogComponent;
import ver8.view.Wishfull.dialogs.GameSelect.GameSelect;

public class JoinGame extends SyncedGames implements DialogComponent {
    private final GameSettings gameSettings;
    private final GameSelect gameSelect;

    public JoinGame(GameSelect gameSelect) {
        super(null, "Join Game", gameSelect, SyncedListType.JOINABLE_GAMES);
        this.gameSettings = gameSelect.getGameSettings();
        this.gameSelect = gameSelect;
        setOnSelect(selected -> {
            gameSettings.setJoiningToGameID(((SelectableGame) selected).gameInfo.gameId);
        });
        doneAdding();
    }

    @Override
    public void onOk() {
        gameSettings.setGameType(GameSettings.GameType.JOIN_EXISTING);
        gameSelect.done();
    }


    @Override
    public Parent parent() {
        return parent;
    }
}
