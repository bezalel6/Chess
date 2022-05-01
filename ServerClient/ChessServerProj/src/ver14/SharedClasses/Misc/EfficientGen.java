package ver14.SharedClasses.Misc;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Efficient gen - represents an object that will only generate a new value when a key changes its value.
 * used to save performance on calculating data that hasn't changed
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class EfficientGen<K, V> {
    /**
     * The Generator for creating a new value.
     */
    private final Supplier<V> generator;
    /**
     * The Old key.
     */
    private K oldKey;
    /**
     * The Old value.
     */
    private V oldValue;


    /**
     * Instantiates a new Efficient gen.
     *
     * @param generator the generator
     */
    public EfficientGen(Supplier<V> generator) {
        this.generator = generator;
    }

    /**
     * Get v.
     *
     * @param currentKey the current key
     * @return the v
     */
    public V get(K currentKey) {
        boolean eq = checkEquals(oldKey, currentKey);

        if (eq) {
            return oldValue;
        }
        oldValue = generator.get();
        oldKey = currentKey;
        return oldValue;
    }

    protected boolean checkEquals(K key1, K key2) {
        return Objects.equals(key1, key2);
    }
}
