package ver7.view.List.Synced;

import ver7.SharedClasses.Sync.SyncedItems;
import ver7.SharedClasses.Sync.SyncedListType;
import ver7.view.List.ComponentsList;

public interface SyncableList {
    SyncedListType syncedListType();

    void sync(SyncedItems items);

    ComponentsList list();

}
