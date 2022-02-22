package ver8.view.OldDialogs.GameSelect.Panels;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.view.OldDialogs.GameSelect.GameSelect;
import ver8.view.OldDialogs.GameSelect.buttons.SyncedSelectablesListOldOld;
import ver8.view.OldDialogs.GameSelect.selectable.SelectableGame;
import ver8.view.OldDialogs.Navigation.BackOkInterface;
import ver8.view.OldDialogs.Navigation.OldNavable;

public class ResumeGames extends OldNavable {
    private final SyncedSelectablesListOldOld list;

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
        list = new SyncedSelectablesListOldOld("select game", selected -> {
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
