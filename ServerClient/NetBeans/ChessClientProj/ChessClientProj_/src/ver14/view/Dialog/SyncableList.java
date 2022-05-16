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
     * get this synced list's type.
     *
     * @return the synced list type
     */
    SyncedListType syncedListType();

    /**
     * Sync this list with {@code items}.
     *
     * @param items the items
     */
    void sync(SyncedItems items);
}
