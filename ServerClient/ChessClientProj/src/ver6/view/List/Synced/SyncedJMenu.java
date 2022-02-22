package ver6.view.List.Synced;

import ver6.SharedClasses.Sync.SyncedItems;
import ver6.SharedClasses.Sync.SyncedListType;
import ver6.view.List.ComponentsList;
import ver6.view.List.JMenuCompList;
import ver6.view.dialogs.SelectedCallBack;

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
