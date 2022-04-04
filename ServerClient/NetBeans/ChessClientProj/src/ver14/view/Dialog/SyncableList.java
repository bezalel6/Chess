package ver14.view.Dialog;

import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Sync.SyncedListType;

public interface SyncableList {
    SyncedListType syncedListType();

    void sync(SyncedItems items);
}
