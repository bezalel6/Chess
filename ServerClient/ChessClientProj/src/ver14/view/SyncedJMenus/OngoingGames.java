package ver14.view.SyncedJMenus;

import ver14.SharedClasses.Sync.SyncedListType;
import ver14.view.Dialog.Dialogs.Header;

public class OngoingGames extends SyncedJMenu {
    public OngoingGames() {
        super(new Header("Ongoing Games"), SyncedListType.ONGOING_GAMES);
    }

    @Override
    protected boolean canUseIcon() {
        return false;
    }
}
