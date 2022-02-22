package ver5.view.dialogs.game_select.Panels;

import ver5.SharedClasses.GameSettings;
import ver5.SharedClasses.messages.SyncedListType;
import ver5.view.dialogs.Navigation.BackOkInterface;
import ver5.view.dialogs.WinPnl;
import ver5.view.dialogs.game_select.GameSelect;
import ver5.view.dialogs.game_select.buttons.SyncedList;
import ver5.view.dialogs.game_select.selectable.SelectableGame;

public class ResumeGames extends WinPnl {
    public ResumeGames(GameSelect gameSelect) {
        super("resume game");
        GameSettings gameSettings = gameSelect.getGameSettings();
        initBackOk(new BackOkInterface() {
            @Override
            public void onBack() {
                gameSelect.switchToStart();
            }

            @Override
            public void onOk() {
                gameSettings.setGameType(GameSettings.GameType.RESUME);
                gameSelect.done();
            }
        });
        add(new SyncedList("select game", selected -> {
            gameSettings.setResumingGameID(((SelectableGame) selected).SavedGame.gameId);
        }, gameSelect, SyncedListType.RESUMABLE));
        doneAdding();
    }
}
