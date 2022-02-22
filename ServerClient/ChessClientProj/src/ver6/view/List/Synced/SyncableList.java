package ver6.view.List.Synced;

import ver6.SharedClasses.Sync.SyncedItems;
import ver6.SharedClasses.Sync.SyncedListType;
import ver6.view.List.ComponentsList;

public interface SyncableList {
    SyncedListType syncedListType();

    void sync(SyncedItems items);

    ComponentsList list();

}
