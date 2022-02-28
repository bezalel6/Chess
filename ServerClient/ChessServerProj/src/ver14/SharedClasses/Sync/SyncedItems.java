package ver14.SharedClasses.Sync;

import org.jetbrains.annotations.NotNull;
import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.SavedGames.GameInfo;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class SyncedItems<E extends SyncableItem> extends ConcurrentHashMap<String, E> {
    public final static SyncedItems<UserInfo> exampleUsers1 = new SyncedItems<>(SyncedListType.CONNECTED_USERS) {{
        for (int i = 1; i <= 5; i++) {
            add(new UserInfo("id " + i));
        }
    }};
    public final static SyncedItems exampleUsers2 = new SyncedItems(SyncedListType.CONNECTED_USERS) {{
        for (int i = 1; i <= 5; i++) {
            add(new UserInfo("id " + i));
        }
    }};
    public final static SyncedItems exampleGames1 = new SyncedItems(SyncedListType.RESUMABLE_GAMES) {{
        for (int i = 1; i <= 10; i++) {
            add(GameInfo.example());
        }
    }};
    public final static SyncedItems exampleGames2 = new SyncedItems(SyncedListType.JOINABLE_GAMES) {{
        for (int i = 1; i <= 5; i++) {
            add(GameInfo.example());
        }
    }};
    public final SyncedListType syncedListType;
    //only used by the server ↓
    private final Callback<SyncedItems<E>> onUpdate;

    public SyncedItems(SyncedListType syncedListType) {
        this(syncedListType, null);
    }

    public SyncedItems(SyncedListType syncedListType, Callback<SyncedItems<E>> onUpdate) {
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

    public void addAll(Collection<? extends SyncableItem> adding) {
        adding.forEach(val -> add((E) val));
    }

    public void add(E value) {
        put(value);
    }

    public SyncableItem put(E value) {
        return put(value.ID(), value);
    }

    @Override
    public E put(@NotNull String key, @NotNull E value) {
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
