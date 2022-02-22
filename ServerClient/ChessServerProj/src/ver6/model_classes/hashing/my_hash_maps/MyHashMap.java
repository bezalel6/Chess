package ver6.model_classes.hashing.my_hash_maps;

import ver6.SharedClasses.Hashable;
import ver6.model_classes.hashing.HashManager;

import java.util.concurrent.ConcurrentHashMap;


public class MyHashMap extends ConcurrentHashMap<Long, Hashable> {
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
    public Hashable put(Long key, Hashable value) {
        if (maxSize.size == 0) {
            return null;
        }
        size++;
        verifySize();
        return super.put(key, value);
    }
}
