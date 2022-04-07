package ver14.SharedClasses.Threads.ErrorHandling;

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
