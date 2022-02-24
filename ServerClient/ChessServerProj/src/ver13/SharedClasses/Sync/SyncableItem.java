package ver13.SharedClasses.Sync;

public interface SyncableItem {

    default SyncableItem getSyncableItem() {
        return this;
    }

    String ID();
}
