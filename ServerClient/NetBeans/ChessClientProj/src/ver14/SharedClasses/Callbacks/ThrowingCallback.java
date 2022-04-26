package ver14.SharedClasses.Callbacks;


/**
 * Throwing callback - a callback that might throw an exception .
 *
 * @param <T> the callback type
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface ThrowingCallback<T> {


    /**
     * Callback.
     *
     * @param obj the obj
     * @throws Exception the exception
     */
    void callback(T obj) throws Exception;

}
