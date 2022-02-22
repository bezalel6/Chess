package ver12.view.Dialog;

import ver12.SharedClasses.Sync.SyncedItems;
import ver12.SharedClasses.Sync.SyncedListType;

public interface SyncableList {
    SyncedListType syncedListType();

    void sync(SyncedItems items);
}
