package ver14.view.Dialog.Dialogs.GameSelection.Components;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.view.Dialog.BackOk.BackOkInterface;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Components.SyncableListComponent;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.SelectableGame;

/**
 * represents a Synchronized list of games.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class SyncedGamesList extends SyncableListComponent implements BackOkInterface {
    /**
     * The Game settings.
     */
    protected final GameSettings gameSettings;
    /**
     * The Game type.
     */
    protected final ver14.SharedClasses.Game.GameSetup.GameType gameType;

    /**
     * Instantiates a new Synced games list.
     *
     * @param header       the header
     * @param listType     the list type
     * @param parent       the parent
     * @param gameSettings the game settings
     * @param gameType     the game type
     */
    public SyncedGamesList(Header header, SyncedListType listType, Parent parent, GameSettings gameSettings, ver14.SharedClasses.Game.GameSetup.GameType gameType) {
        super(header, listType, parent);
        this.gameSettings = gameSettings;
        this.gameType = gameType;
    }

    @Override
    protected void onSelected() {
        String id = null;
        if (selected != null) {
            id = ((SelectableGame) selected).getGameInfo().gameId;
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
