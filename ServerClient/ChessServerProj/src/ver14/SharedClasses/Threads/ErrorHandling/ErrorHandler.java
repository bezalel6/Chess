package ver14.SharedClasses.Threads.ErrorHandling;

import ver14.SharedClasses.Threads.MyThread;

/*
 * ErrorHandler
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * ErrorHandler -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * ErrorHandler -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

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
     * Handle.
     *
     * @param err the err
     */
    void handle(MyError err);
}
