package ver9.view.Dialog;

import ver9.SharedClasses.Sync.SyncedItems;
import ver9.SharedClasses.Sync.SyncedListType;

public interface SyncableList {
    SyncedListType syncedListType();

    void sync(SyncedItems items);
}
