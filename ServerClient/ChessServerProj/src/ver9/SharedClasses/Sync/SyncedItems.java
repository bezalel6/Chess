package ver9.SharedClasses.Sync;

import ver9.SharedClasses.Callbacks.Callback;
import ver9.SharedClasses.SavedGames.GameInfo;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class SyncedItems extends ConcurrentHashMap<String, SyncableItem> {
    public final static SyncedItems exampleUsers1 = new SyncedItems(SyncedListType.CONNECTED_USERS) {{
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
    private final Callback<SyncedItems> onUpdate;

    public SyncedItems(SyncedListType syncedListType) {
        this(syncedListType, null);
    }

    public SyncedItems(SyncedListType syncedListType, Callback<SyncedItems> onUpdate) {
        this.syncedListType = syncedListType;
        this.onUpdate = onUpdate;
    }

    public void forEachItem(Callback<SyncableItem> action) {
        values().forEach(action::callback);
    }

    public Stream<SyncableItem> stream() {
        return values().stream();
    }

    public SyncedItems clean() {
        SyncedItems ret = new SyncedItems(syncedListType);
        ret.addAll(values());
        return ret;
    }

    public void addAll(Collection<SyncableItem> values) {
        values.forEach(this::add);
    }

    public void add(SyncableItem value) {
        put(value);
    }

    public SyncableItem put(SyncableItem value) {
        return put(value.ID(), value);
    }

    @Override
    public SyncableItem put(String key, SyncableItem value) {
        SyncableItem ret = super.put(key, value);
        updated();
        return ret;
    }

    @Override
    public SyncableItem remove(Object key) {
        SyncableItem ret = super.remove(key);
        updated();
        return ret;
    }

    private void updated() {
        if (onUpdate != null) {
            onUpdate.callback(this);
        }
    }
}
