package ver14.SharedClasses.Misc;

import java.util.Objects;
import java.util.function.Supplier;

public class EfficientGen<K, V> {
    private final Supplier<V> generator;
    private K oldKey;
    private V oldValue;


    public EfficientGen(Supplier<V> generator) {
        this.generator = generator;
    }

    public V get(K currentKey) {
        if (Objects.equals(oldKey, currentKey)) {
            return oldValue;
        }
        oldValue = generator.get();
        oldKey = currentKey;
        return oldValue;
    }
}
