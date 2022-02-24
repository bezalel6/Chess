package ver13.view.Dialog;

import ver13.SharedClasses.Sync.SyncedItems;
import ver13.SharedClasses.Sync.SyncedListType;

public interface SyncableList {
    SyncedListType syncedListType();

    void sync(SyncedItems items);
}
