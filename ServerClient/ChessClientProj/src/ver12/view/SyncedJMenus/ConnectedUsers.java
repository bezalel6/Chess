package ver12.view.SyncedJMenus;

import ver12.SharedClasses.Sync.SyncedListType;
import ver12.view.Dialog.Dialogs.Header;

public class ConnectedUsers extends SyncedJMenu {
    public ConnectedUsers() {
        super(new Header("Connected Users"), SyncedListType.CONNECTED_USERS);
    }
}
