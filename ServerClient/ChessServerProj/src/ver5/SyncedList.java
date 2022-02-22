package ver5;

import ver5.SharedClasses.messages.SyncedListType;
import ver5.server.Server;

import java.util.HashMap;

public class SyncedList<K, V> extends HashMap<K, V> {
    public final SyncedListType syncedListType;
    private final Server server;

    public SyncedList(Server server, SyncedListType syncedListType) {
        this.server = server;
        this.syncedListType = syncedListType;
    }

    @Override
    public V put(K key, V value) {
        V ret = super.put(key, value);
        server.syncList(this);
        return ret;
    }

    @Override
    public V remove(Object key) {
        V ret = super.remove(key);
        server.syncList(this);
        return ret;
    }
}
