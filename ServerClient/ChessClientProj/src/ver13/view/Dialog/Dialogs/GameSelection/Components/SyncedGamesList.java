package ver13.view.Dialog.Dialogs.GameSelection.Components;

import ver13.SharedClasses.GameSettings;
import ver13.SharedClasses.Sync.SyncedListType;
import ver13.view.Dialog.Components.Parent;
import ver13.view.Dialog.Components.SyncableListComponent;
import ver13.view.Dialog.Dialogs.BackOkInterface;
import ver13.view.Dialog.Dialogs.Header;
import ver13.view.Dialog.Selectables.Game;

public abstract class SyncedGamesList extends SyncableListComponent implements BackOkInterface {
    protected final GameSettings gameSettings;
    protected final GameSettings.GameType gameType;

    public SyncedGamesList(Header header, SyncedListType listType, Parent parent, GameSettings gameSettings, GameSettings.GameType gameType) {
        super(header, listType, parent);
        this.gameSettings = gameSettings;
        this.gameType = gameType;
    }

    @Override
    protected void onSelected() {
        String id = null;
        if (selected != null) {
            id = ((Game) selected).getGameInfo().gameId;
        }
        gameSettings.setGameID(id);
    }

    @Override
    public void onBack() {
        parent.back();
    }

    @Override
    public void onOk() {
        gameSettings.setGameType(gameType);
        parent.done();
    }
}