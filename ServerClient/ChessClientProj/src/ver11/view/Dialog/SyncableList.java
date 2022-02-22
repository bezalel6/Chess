package ver11.view.Dialog;

import ver11.SharedClasses.Sync.SyncedItems;
import ver11.SharedClasses.Sync.SyncedListType;

public interface SyncableList {
    SyncedListType syncedListType();

    void sync(SyncedItems items);
}
