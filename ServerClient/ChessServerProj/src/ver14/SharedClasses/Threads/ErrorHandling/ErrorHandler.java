package ver14.SharedClasses.Threads.ErrorHandling;

import ver14.SharedClasses.Threads.MyThread;


/**
 * represents a callback that can handle a certain type of errors.
 */
public interface ErrorHandler {
    /**
     * Ignore any error that might get thrown while running the runnable.
     *
     * @param runnable the runnable to run
     * @return true if the runnable threw, false otherwise
     */
    static boolean ignore(ThrowingRunnable runnable) {

        Thread currentThread = Thread.currentThread();
        MyThread myThread = null;
        if (currentThread instanceof MyThread m) {
            myThread = m;
            myThread.ignoreErrs();
        }

        boolean ret;
        try {
            runnable.run();
            ret = false;
        } catch (Throwable t) {
            ret = true;
        }
        if (myThread != null)
            myThread.reactivateErrs();
        return ret;


    }

    /**
     * handle an error that was thrown.
     *
     * @param err the error thrown
     */
    void handle(MyError err);
}
