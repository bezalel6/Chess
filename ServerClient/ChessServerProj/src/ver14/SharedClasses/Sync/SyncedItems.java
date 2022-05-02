package ver14.SharedClasses.Sync;

import org.jetbrains.annotations.NotNull;
import ver14.SharedClasses.Callbacks.Callback;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


/**
 * represents a list of Synced items.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class SyncedItems<E extends SyncableItem> extends ConcurrentHashMap<String, E> {

    /**
     * The Synced list type.
     */
    public final SyncedListType syncedListType;
    /**
     * The On update.
     */
    private final Callback<SyncedItems<E>> onUpdate;

    /**
     * Instantiates a new Synced items.
     *
     * @param syncedListType the synced list type
     */
    public SyncedItems(SyncedListType syncedListType) {
        this(syncedListType, null);
    }

    /**
     * Instantiates a new Synced items.
     *
     * @param syncedListType the synced list type
     * @param onUpdate       the on update
     */
    public SyncedItems(SyncedListType syncedListType, Callback<SyncedItems<E>> onUpdate) {
        this.syncedListType = syncedListType;
        this.onUpdate = onUpdate;
    }

    /**
     * For each item.
     *
     * @param action the action
     */
    public synchronized void forEachItem(Callback<E> action) {
        values().forEach(action::callback);
    }

    /**
     * Stream stream.
     *
     * @return the stream
     */
    public Stream<E> stream() {
        return values().stream();
    }

    /**
     * Clean synced items.
     *
     * @return the synced items
     */
    public SyncedItems<E> clean() {
        SyncedItems<E> ret = new SyncedItems<>(syncedListType);
        ret.addAll(values());
        return ret;
    }

    /**
     * Add all.
     *
     * @param adding the adding
     */
    public void addAll(Collection<? extends SyncableItem> adding) {
        adding.forEach(val -> add((E) val));
    }

    /**
     * Add.
     *
     * @param value the value
     */
    public synchronized void add(E value) {
        put(value);
    }

    /**
     * Put syncable item.
     *
     * @param value the value
     * @return the syncable item
     */
    public synchronized SyncableItem put(E value) {
        return put(value.ID(), value);
    }

    /**
     * Put e.
     *
     * @param key   the key
     * @param value the value
     * @return the e
     */
    @Override
    public synchronized E put(@NotNull String key, @NotNull E value) {
        E ret = super.put(key, value);
        updated();
        return ret;
    }

    /**
     * Remove e.
     *
     * @param key the key
     * @return the e
     */
    @Override
    public synchronized E remove(Object key) {
        E ret = super.remove(key);
        if (ret != null) updated();
        return ret;
    }

    /**
     * Remove boolean.
     *
     * @param key   the key
     * @param value the value
     * @return the boolean
     */
    @Override
    public synchronized boolean remove(Object key, Object value) {

        boolean ret = super.remove(key, value);
        updated();

        assert ret;
        return true;
    }

    /**
     * Updated.
     */
    private void updated() {
        if (onUpdate != null) {
            onUpdate.callback(this);
        }
    }

    /**
     * Batch remove.
     *
     * @param remover the remover
     */
    public void batchRemove(Remover<E> remover) {
        List<E> del = values()
                .stream()
                .filter(remover::remove)
                .toList();
        del.forEach(deleting -> super.remove(deleting.ID()));
        updated();
    }

    /**
     * Remover.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public interface Remover<L> {
        /**
         * Remove boolean.
         *
         * @param item the item
         * @return the boolean
         */
        boolean remove(L item);
    }
}
