package ver14.view.SyncedJMenus;

import ver14.SharedClasses.Sync.SyncedListType;
import ver14.view.Dialog.Dialogs.Header;

/**
 * a synchronized list of all Ongoing games.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class OngoingGames extends SyncedJMenu {
    /**
     * Instantiates a new Ongoing games.
     */
    public OngoingGames() {
        super(new Header("Ongoing Games"), SyncedListType.ONGOING_GAMES);
    }

    @Override
    protected boolean canUseIcon() {
        return false;
    }
}
