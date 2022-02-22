package ver5.view.dialogs.game_select.Panels;

import ver5.SharedClasses.GameSettings;
import ver5.SharedClasses.messages.SyncedListType;
import ver5.view.dialogs.Navigation.BackOkInterface;
import ver5.view.dialogs.WinPnl;
import ver5.view.dialogs.game_select.GameSelect;
import ver5.view.dialogs.game_select.buttons.SyncedList;
import ver5.view.dialogs.game_select.selectable.SelectableGame;

public class JoinGame extends WinPnl {
    public JoinGame(GameSelect gameSelect) {
        super("join game");
        GameSettings gameSettings = gameSelect.getGameSettings();
        initBackOk(new BackOkInterface() {
            @Override
            public void onBack() {
                gameSelect.switchToStart();
            }

            @Override
            public void onOk() {
                gameSettings.setGameType(GameSettings.GameType.JOIN_EXISTING);
                gameSelect.done();
            }
        });
        add(new SyncedList("select game", selected -> {
            gameSettings.setJoiningToGameID(((SelectableGame) selected).SavedGame.gameId);
        }, gameSelect, SyncedListType.JOINABLE));
        doneAdding();
    }
}
