package ver14.SharedClasses.Callbacks;


/**
 * Callback - represents an asynchronous callback with an object.
 * some actions to execute at an unknown point in the future.
 * used for things like button clicks.
 *
 * @param <T> the object's type
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface Callback<T> {
    /**
     * Callback.
     *
     * @param obj the parameter of type {@link T}
     */
    void callback(T obj);
}
