package ver8.view.Wishfull.List.Synced;

import ver8.SharedClasses.Sync.SyncedItems;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.view.Wishfull.List.ComponentsList;

public interface SyncableList {
    SyncedListType syncedListType();

    void sync(SyncedItems items);

    ComponentsList list();

}
