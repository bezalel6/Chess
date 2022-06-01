package ver14.view.SyncedJMenus;

import ver14.SharedClasses.Sync.SyncedListType;
import ver14.view.Dialog.Dialogs.Header;

/**
 * a synchronized list of all Connected users.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ConnectedUsers extends SyncedJMenu {
    /**
     * Instantiates a new Connected users.
     */
    public ConnectedUsers() {
        super(new Header("Connected Users"), SyncedListType.CONNECTED_USERS);
    }
}
