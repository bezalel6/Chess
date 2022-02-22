package ver9.view.SyncedJMenus;

import ver9.SharedClasses.Sync.SyncedListType;
import ver9.view.Dialog.Dialogs.Header;

public class OngoingGames extends SyncedJMenu {
    public OngoingGames() {
        super(new Header("Ongoing Games"), SyncedListType.ONGOING_GAMES);
    }
}
