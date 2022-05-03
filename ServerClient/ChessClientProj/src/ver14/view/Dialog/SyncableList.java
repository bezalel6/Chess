package ver14.view.Dialog;

import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Sync.SyncedListType;

/**
 * represents a Syncable list.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface SyncableList {
    /**
     * Synced list type synced list type.
     *
     * @return the synced list type
     */
    SyncedListType syncedListType();

    /**
     * Sync.
     *
     * @param items the items
     */
    void sync(SyncedItems items);
}
