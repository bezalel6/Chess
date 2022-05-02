package ver14.SharedClasses.Sync;


/**
 * represents a Syncable item.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface SyncableItem {

    /**
     * Gets syncable item.
     *
     * @return the syncable item
     */
    default SyncableItem getSyncableItem() {
        return this;
    }

    /**
     * Id string.
     *
     * @return the string
     */
    String ID();
}
