package ver14.SharedClasses.Threads.ErrorHandling;


/**
 * represents an object that acts as an Environment Manager. logging handled errors as they occur,
 * and safely shutting down if an unhandled error is thrown.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface EnvManager {
    /**
     * notifies manager of a managed error
     *
     * @param err the error thrown
     */
    void handledErr(MyError err);

    /**
     * notifies manager of an un-handleable error. triggering a shutdown
     *
     * @param err the error thrown
     */
    void criticalErr(MyError err);
}
