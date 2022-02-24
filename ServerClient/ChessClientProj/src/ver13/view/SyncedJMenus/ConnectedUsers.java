package ver13.view.SyncedJMenus;

import ver13.SharedClasses.Sync.SyncedListType;
import ver13.view.Dialog.Dialogs.Header;

public class ConnectedUsers extends SyncedJMenu {
    public ConnectedUsers() {
        super(new Header("Connected Users"), SyncedListType.CONNECTED_USERS);
    }
}
