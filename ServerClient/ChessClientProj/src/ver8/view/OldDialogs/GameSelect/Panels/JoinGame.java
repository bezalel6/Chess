package ver8.view.OldDialogs.GameSelect.Panels;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.view.OldDialogs.GameSelect.GameSelect;
import ver8.view.OldDialogs.GameSelect.buttons.SyncedSelectablesListOldOld;
import ver8.view.OldDialogs.GameSelect.selectable.SelectableGame;
import ver8.view.OldDialogs.Navigation.BackOkInterface;
import ver8.view.OldDialogs.Navigation.OldNavable;

public class JoinGame extends OldNavable {
    private final SyncedSelectablesListOldOld list;

    public JoinGame(GameSelect gameSelect) {
        super("join game", gameSelect, "Join existing game");
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
        list = new SyncedSelectablesListOldOld("select game", selected -> {
            gameSettings.setJoiningToGameID(((SelectableGame) selected).gameInfo.gameId);
        }, gameSelect, SyncedListType.JOINABLE_GAMES);
        addList(list);
        doneAdding();
    }

    @Override
    public void doneAdding() {
        super.doneAdding();
        list.setNavParent(this);
    }
}
