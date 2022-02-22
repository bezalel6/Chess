package ver11.view.SyncedJMenus;

import ver11.SharedClasses.Sync.SyncedListType;
import ver11.view.Dialog.Dialogs.Header;

public class OngoingGames extends SyncedJMenu {
    public OngoingGames() {
        super(new Header("Ongoing Games"), SyncedListType.ONGOING_GAMES);
    }

    @Override
    protected boolean canUseIcon() {
        return false;
    }
}
