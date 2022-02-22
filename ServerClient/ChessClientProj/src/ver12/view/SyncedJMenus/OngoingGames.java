package ver12.view.SyncedJMenus;

import ver12.SharedClasses.Sync.SyncedListType;
import ver12.view.Dialog.Dialogs.Header;

public class OngoingGames extends SyncedJMenu {
    public OngoingGames() {
        super(new Header("Ongoing Games"), SyncedListType.ONGOING_GAMES);
    }

    @Override
    protected boolean canUseIcon() {
        return false;
    }
}
