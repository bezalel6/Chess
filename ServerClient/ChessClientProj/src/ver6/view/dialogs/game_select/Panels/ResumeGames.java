package ver6.view.dialogs.game_select.Panels;

import ver6.SharedClasses.GameSettings;
import ver6.SharedClasses.Sync.SyncedListType;
import ver6.view.dialogs.Navigation.BackOkInterface;
import ver6.view.dialogs.Navigation.OldNavable;
import ver6.view.dialogs.game_select.GameSelect;
import ver6.view.dialogs.game_select.buttons.SyncedSelectablesList;
import ver6.view.dialogs.game_select.selectable.SelectableGame;

public class ResumeGames extends OldNavable {
    private final SyncedSelectablesList list;

    public ResumeGames(GameSelect gameSelect) {
        super("resume game", gameSelect, "Resume unfinished game");
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
        list = new SyncedSelectablesList("select game", selected -> {
            gameSettings.setResumingGameID(((SelectableGame) selected).gameInfo.gameId);
        }, gameSelect, SyncedListType.RESUMABLE_GAMES);
        addList(list);
        doneAdding();
    }

    @Override
    public void doneAdding() {
        super.doneAdding();
        list.setNavParent(this);
    }
}
