package ver12.Model.hashing.my_hash_maps;

import ver12.Model.hashing.HashManager;

import java.util.concurrent.ConcurrentHashMap;


public class MyHashMap<E> extends ConcurrentHashMap<Long, E> {
    private final HashManager.Size maxSize;
    private long size;

    public MyHashMap(HashManager.Size maxSize) {
        this.maxSize = maxSize;
        size = 0;
        HashManager.allMaps.add(this);
    }

    @Override
    public boolean containsKey(Object key) {
        return maxSize.size != 0 && verifySize() && super.containsKey(key);
    }

    private boolean verifySize() {
        if (size >= maxSize.size) {
            clear();
            return false;
        }
        return true;
    }

    @Override
    public E put(Long key, E value) {
        if (maxSize.size == 0) {
            return null;
        }
        size++;
        verifySize();
        return super.put(key, value);
    }
}
