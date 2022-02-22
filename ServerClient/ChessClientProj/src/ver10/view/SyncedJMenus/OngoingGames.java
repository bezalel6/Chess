package ver10.view.SyncedJMenus;

import ver10.SharedClasses.Sync.SyncedListType;
import ver10.view.Dialog.Dialogs.Header;

public class OngoingGames extends SyncedJMenu {
    public OngoingGames() {
        super(new Header("Ongoing Games"), SyncedListType.ONGOING_GAMES);
    }

    @Override
    protected boolean canUseIcon() {
        return false;
    }
}
