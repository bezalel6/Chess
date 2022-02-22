package ver7.view.dialogs.GameSelect.Panels;

import ver7.SharedClasses.GameSettings;
import ver7.SharedClasses.Sync.SyncedListType;
import ver7.view.List.Synced.SyncedGames;
import ver7.view.dialogs.GameSelect.GameSelect;
import ver7.view.dialogs.GameSelect.Selectables.SelectableGame;
import ver7.view.dialogs.VerifiedComponent;

public class ResumeGames extends SyncedGames implements VerifiedComponent {
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
}
