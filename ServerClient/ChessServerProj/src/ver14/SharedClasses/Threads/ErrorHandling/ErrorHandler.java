package ver14.SharedClasses.Threads.ErrorHandling;

/**
 * The interface Error handler.
 */
public interface ErrorHandler {

    /**
     * catches anything that might get thrown
     *
     * @param runnable the runnable
     * @return did the runnable throw
     */
    static boolean ignore(ThrowingRunnable runnable) {
        try {
            runnable.run();
            return false;
        } catch (Throwable t) {
            return true;
        }
    }

    void handle(MyError err);
}
