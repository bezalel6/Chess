package ver10.view.Dialog.Dialogs.GameSelection.Components;

import ver10.SharedClasses.GameSettings;
import ver10.SharedClasses.Sync.SyncedListType;
import ver10.view.Dialog.Components.Parent;
import ver10.view.Dialog.Components.SyncableListComponent;
import ver10.view.Dialog.Dialogs.BackOkInterface;
import ver10.view.Dialog.Dialogs.Header;
import ver10.view.Dialog.Selectables.Game;

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