package ver10.view.Dialog;

import ver10.SharedClasses.Sync.SyncedItems;
import ver10.SharedClasses.Sync.SyncedListType;

public interface SyncableList {
    SyncedListType syncedListType();

    void sync(SyncedItems items);
}
