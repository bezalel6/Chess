package ver14.SharedClasses.Sync;


/**
 * represents an item that can be synchronized.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface SyncableItem {

    /**
     * Gets the syncable item. some items might not be compatible
     * for syncing themselves, so they may override this function and create a syncable representation of themselves.
     *
     * @return the syncable item representing the current state of this obj
     */
    default SyncableItem getSyncableItem() {
        return this;
    }

    /**
     * a syncable item must have a unique id.
     *
     * @return the id
     */
    String ID();
}
