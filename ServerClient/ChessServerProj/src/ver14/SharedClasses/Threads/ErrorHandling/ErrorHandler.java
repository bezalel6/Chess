package ver14.SharedClasses.Threads.ErrorHandling;

/**
 * The interface Error handler.
 *
 * @param <E> the type parameter
 */
public interface ErrorHandler<E extends MyError> {


    /**
     * Ignore boolean.
     *
     * @param runnable the runnable
     * @return true if the runnable threw, false otherwise
     */
    static boolean ignore(ThrowingRunnable runnable) {
        try {
            runnable.run();
            return false;
        } catch (Throwable t) {
            return true;
        }
    }

    /**
     * Handle.
     *
     * @param err the err
     */
    void handle(MyError err);
}
