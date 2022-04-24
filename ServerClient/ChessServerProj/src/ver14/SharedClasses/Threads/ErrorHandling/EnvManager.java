package ver14.SharedClasses.Threads.ErrorHandling;


/**
 * Env manager - an object that can handle errors as they occur.
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
     * notifies manager of an un-handleable error. the manager must shut down everything
     *
     * @param err the error thrown
     */
    void criticalErr(MyError err);
}
