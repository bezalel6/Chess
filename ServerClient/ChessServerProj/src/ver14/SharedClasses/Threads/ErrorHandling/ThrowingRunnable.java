package ver14.SharedClasses.Threads.ErrorHandling;


/**
 * represents a runnable that might throw an error.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface ThrowingRunnable {
    /**
     * Run.
     *
     * @throws Throwable the throwable that might get thrown
     */
    void run() throws Throwable;
}
