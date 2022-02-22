package ver8.view.List.Synced;

import ver8.SharedClasses.Sync.SyncedItems;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.view.List.OldComponentsList;

public interface OldSyncableList {
    SyncedListType syncedListType();

    void sync(SyncedItems items);

    OldComponentsList getComponentsList();

}
