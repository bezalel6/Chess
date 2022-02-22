package ver8.view.Wishfull.dialogs.GameSelect.Panels;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.view.Wishfull.List.Synced.SyncedGames;
import ver8.view.Wishfull.dialogs.GameSelect.Selectables.SelectableGame;
import ver8.view.Wishfull.dialogs.DialogComponent;
import ver8.view.Wishfull.dialogs.GameSelect.GameSelect;

public class ResumeGames extends SyncedGames implements DialogComponent {
    private final GameSettings gameSettings;
    private final GameSelect gameSelect;

    public ResumeGames(GameSelect gameSelect) {
        super(null, "Resume Game", gameSelect, SyncedListType.RESUMABLE_GAMES);
        this.gameSettings = gameSelect.getGameSettings();
        this.gameSelect = gameSelect;
        setOnSelect(selected -> {
            gameSettings.setResumingGameID(((SelectableGame) selected).gameInfo.gameId);
        });
        doneAdding();
    }

    @Override
    public void onOk() {
        gameSettings.setGameType(GameSettings.GameType.RESUME);
        gameSelect.done();
    }

    @Override
    public Parent parent() {
        return parent;
    }
}
