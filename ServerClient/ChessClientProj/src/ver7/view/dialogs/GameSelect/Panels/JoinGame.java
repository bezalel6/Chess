package ver7.view.dialogs.GameSelect.Panels;

import ver7.SharedClasses.GameSettings;
import ver7.SharedClasses.Sync.SyncedListType;
import ver7.view.List.Synced.SyncedGames;
import ver7.view.dialogs.GameSelect.GameSelect;
import ver7.view.dialogs.GameSelect.Selectables.SelectableGame;

public class JoinGame extends SyncedGames {
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


}
