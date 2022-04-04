package ver14.view.SyncedJMenus;

import ver14.SharedClasses.Sync.SyncedListType;
import ver14.view.Dialog.Dialogs.Header;

public class ConnectedUsers extends SyncedJMenu {
    public ConnectedUsers() {
        super(new Header("Connected Users"), SyncedListType.CONNECTED_USERS);
    }
}
