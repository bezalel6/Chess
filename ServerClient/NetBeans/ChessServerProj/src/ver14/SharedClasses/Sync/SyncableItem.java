package ver14.SharedClasses.Sync;

public interface SyncableItem {

    default SyncableItem getSyncableItem() {
        return this;
    }

    String ID();
}
