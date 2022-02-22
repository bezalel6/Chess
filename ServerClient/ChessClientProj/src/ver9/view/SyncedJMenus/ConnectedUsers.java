package ver9.view.SyncedJMenus;

import ver9.SharedClasses.Sync.SyncedListType;
import ver9.view.Dialog.Dialogs.Header;

public class ConnectedUsers extends SyncedJMenu {
    public ConnectedUsers() {
        super(new Header("Connected Users"), SyncedListType.CONNECTED_USERS);
    }
}
