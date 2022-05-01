package ver14.SharedClasses.Callbacks;


/**
 * represents a callback that might throw an exception .
 *
 * @param <T> the callback type
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface ThrowingCallback<T> {


    /**
     * Callback.
     *
     * @param obj the obj
     * @throws Exception the exception that might get thrown
     */
    void callback(T obj) throws Exception;

}
