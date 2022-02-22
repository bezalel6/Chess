package ver8.view.Wishfull.List.Synced;

import ver8.SharedClasses.Sync.SyncedItems;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.view.Wishfull.List.ComponentsList;
import ver8.view.Wishfull.List.JMenuCompList;
import ver8.view.Wishfull.dialogs.SelectedCallBack;
import ver8.view.Wishfull.dialogs.WinPnl;

public class SyncedJMenu extends JMenuCompList implements SyncableList {
    private final SyncedListType syncedListType;

    public SyncedJMenu(String header, SelectedCallBack onSelect, SyncedListType syncedListType) {
        super(header, onSelect);
        this.syncedListType = syncedListType;
    }

    @Override
    public SyncedListType syncedListType() {
        return syncedListType;
    }

    @Override
    public void sync(SyncedItems items) {
        super.sync(items);
    }

    @Override
    public ComponentsList list() {
        return this;
    }

    @Override
    public WinPnl navTo() {
        return null;
    }
}
