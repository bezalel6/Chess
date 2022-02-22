package ver8.view.List.Synced;

import ver8.SharedClasses.Sync.SyncedItems;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.view.List.JMenuCompListOld;
import ver8.view.List.OldComponentsList;
import ver8.view.OldDialogs.SelectedCallBack;

public class SyncedJMenu extends JMenuCompListOld implements OldSyncableList {
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
    public OldComponentsList getComponentsList() {
        return this;
    }
}
