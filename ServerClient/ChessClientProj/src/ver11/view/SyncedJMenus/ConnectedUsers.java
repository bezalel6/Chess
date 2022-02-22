package ver11.view.SyncedJMenus;

import ver11.SharedClasses.Sync.SyncedListType;
import ver11.view.Dialog.Dialogs.Header;

public class ConnectedUsers extends SyncedJMenu {
    public ConnectedUsers() {
        super(new Header("Connected Users"), SyncedListType.CONNECTED_USERS);
    }
}
