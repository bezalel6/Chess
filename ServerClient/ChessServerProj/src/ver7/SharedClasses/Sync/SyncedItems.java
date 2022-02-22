package ver7.SharedClasses.Sync;

import ver7.SharedClasses.Callbacks.Callback;
import ver7.SharedClasses.SavedGames.GameInfo;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class SyncedItems<E extends SyncableItem> extends ConcurrentHashMap<String, E> {
    public final static SyncedItems<UserInfo> exampleUsers1 = new SyncedItems<>(SyncedListType.CONNECTED_PLAYERS) {{
        for (int i = 1; i <= 5; i++) {
            add(new UserInfo("id " + i));
        }
    }};
    public final static SyncedItems<UserInfo> exampleUsers2 = new SyncedItems<>(SyncedListType.CONNECTED_PLAYERS) {{
        for (int i = 1; i <= 5; i++) {
            add(new UserInfo("id " + i));
        }
    }};
    public final static SyncedItems<GameInfo> exampleGames1 = new SyncedItems<>(SyncedListType.RESUMABLE_GAMES) {{
        for (int i = 1; i <= 2; i++) {
            add(GameInfo.example());
        }
    }};
    public final static SyncedItems<GameInfo> exampleGames2 = new SyncedItems<>(SyncedListType.JOINABLE_GAMES) {{
        for (int i = 1; i <= 5; i++) {
            add(GameInfo.example());
        }
    }};
    public final SyncedListType syncedListType;
    private final Callback<SyncedItems> onUpdate;

    public SyncedItems(SyncedListType syncedListType) {
        this(syncedListType, null);
    }

    public SyncedItems(SyncedListType syncedListType, Callback<SyncedItems> onUpdate) {
        this.syncedListType = syncedListType;
        this.onUpdate = onUpdate;
    }

    public void forEachItem(Callback<E> action) {
        values().forEach(action::callback);
    }

    public Stream<E> stream() {
        return values().stream();
    }

    public SyncedItems<E> clean() {
        SyncedItems<E> ret = new SyncedItems<>(syncedListType);
        ret.addAll(values());
        return ret;
    }

    public void addAll(Collection<E> values) {
        values.forEach(this::add);
    }

    public void add(E value) {
        put(value);
    }

    public E put(E value) {
        return put(value.ID(), value);
    }

    @Override
    public E put(String key, E value) {
        E ret = super.put(key, value);
        updated();
        return ret;
    }

    @Override
    public E remove(Object key) {
        E ret = super.remove(key);
        updated();
        return ret;
    }

    private void updated() {
        if (onUpdate != null) {
            onUpdate.callback(this);
        }
    }
}
