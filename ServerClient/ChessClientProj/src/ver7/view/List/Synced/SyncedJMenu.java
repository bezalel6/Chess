package ver7.view.List.Synced;

import ver7.SharedClasses.Sync.SyncedItems;
import ver7.SharedClasses.Sync.SyncedListType;
import ver7.view.List.ComponentsList;
import ver7.view.List.JMenuCompList;
import ver7.view.dialogs.SelectedCallBack;

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
}
