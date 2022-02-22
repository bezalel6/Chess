package ver10.view.SyncedJMenus;

import ver10.SharedClasses.Sync.SyncedListType;
import ver10.view.Dialog.Dialogs.Header;

public class ConnectedUsers extends SyncedJMenu {
    public ConnectedUsers() {
        super(new Header("Connected Users"), SyncedListType.CONNECTED_USERS);
    }
}
