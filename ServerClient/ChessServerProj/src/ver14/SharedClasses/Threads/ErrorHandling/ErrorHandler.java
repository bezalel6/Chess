package ver14.SharedClasses.Threads.ErrorHandling;

import java.util.Vector;

/**
 * The interface Error handler.
 */
public interface ErrorHandler {
    Vector<Long> dontThrowIds = new Vector<>();

    /**
     * catches anything that might get thrown
     *
     * @param runnable the runnable
     * @return did the runnable throw
     */
    static boolean ignore(ThrowingRunnable runnable) {
        boolean ret;
        dontThrowIds.add(Thread.currentThread().getId());
        try {
            runnable.run();
            ret = false;
        } catch (Throwable t) {
            ret = true;
        }
        dontThrowIds.remove(Thread.currentThread().getId());
        return ret;
    }

    static boolean canThrow() {
        return !dontThrowIds.contains(Thread.currentThread().getId());
    }

    void handle(MyError err);
}
