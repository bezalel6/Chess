package ver14.SharedClasses.Threads;

import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Threads.ErrorHandling.ThrowingRunnable;

import java.util.ArrayList;


/**
 * represents a manager that manages all the threads in the enviornment.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ThreadsManager {
    /**
     * The Threads.
     */
    private final static ArrayList<MyThread> threads;


    static {
        threads = new ArrayList<>();
    }


    /**
     * Add thread.
     *
     * @param thread the thread
     */
    public static void addThread(MyThread thread) {
        threads.add(thread);
    }


    /**
     * Stop all.
     */
    public static void stopAll() {
        new Thread(() -> {
            int ms = 1500;
            System.out.println("pausing for " + ms + " ms before stopping all threads");

            try {
                Thread.sleep(ms);
                threads.forEach(MyThread::stopRun);
            } catch (Exception e) {
                System.out.println("stopping threads");
                System.out.println(e.getClass());
            }
        }).start();
    }

    /**
     * Handle errors for a runnable
     *
     * @param runnable the runnable
     */
    public static void handleErrors(ThrowingRunnable runnable) {
        MyError err = null;
        try {
            runnable.run();
        } catch (InterruptedException ignored) {
        } catch (MyError e) {
            err = e;
        } catch (Throwable throwable) {
            err = new MyError(throwable);
        }
        if (err != null && !(Thread.currentThread() instanceof MyThread m && m.isIgnoreErrs()))
            throw err;
    }

    /**
     * Create thread my thread.
     *
     * @param runnable the runnable
     * @param start    the start
     * @return the my thread
     */
    public static MyThread createThread(ThrowingRunnable runnable, boolean start) {
        MyThread thread = new HandledThread(runnable);
        if (start)
            thread.start();
        return thread;
    }


}
