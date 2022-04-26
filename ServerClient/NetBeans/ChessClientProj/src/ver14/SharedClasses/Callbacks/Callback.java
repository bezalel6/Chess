package ver14.SharedClasses.Callbacks;


/**
 * Callback - an object callback.
 *
 * @param <T> the object's type
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface Callback<T> {
    /**
     * Callback.
     *
     * @param obj the obj
     */
    void callback(T obj);
}
