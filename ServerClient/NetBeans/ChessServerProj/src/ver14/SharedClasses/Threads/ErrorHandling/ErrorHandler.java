package ver14.SharedClasses.Threads.ErrorHandling;

import java.util.ArrayList;

/**
 * The interface Error handler.
 */
public interface ErrorHandler {
    ArrayList<Long> dontThrowIds = new ArrayList<>();

    /**
     * catches anything that might get thrown
     *
     * @param runnable the runnable
     * @return did the runnable throw
     */
    static boolean ignore(ThrowingRunnable runnable) {
        boolean ret;
        synchronized (dontThrowIds) {
            dontThrowIds.add(Thread.currentThread().getId());
        }
        try {
            runnable.run();
            ret = false;
        } catch (Throwable t) {
            ret = true;
        }
        synchronized (dontThrowIds) {
            dontThrowIds.remove(Thread.currentThread().getId());
        }
        return ret;
    }

    static boolean canThrow() {
        synchronized (dontThrowIds) {
            return !dontThrowIds.contains(Thread.currentThread().getId());
        }
    }

    void handle(MyError err);
}
