package ver14.SharedClasses.Threads;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Threads.ErrorHandling.EnvManager;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorHandler;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Threads.ErrorHandling.ThrowingRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Threads manager.
 */
public class ThreadsManager {
    private final static ArrayList<MyThread> threads;


    static {
        threads = new ArrayList<>();
    }


    /**
     * Stop all.
     */
    public static void stopAll() {
        try {
            threads.forEach(MyThread::stopRun);
        } catch (Exception e) {
            System.out.println("stopping threads");
            System.out.println(e.getClass());
        }
    }

    /**
     * Handle errors.
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
        if (err != null && !(Thread.currentThread() instanceof MyThread m && m.ignoreErrs))
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


    /**
     * The type My thread.
     */
    public static abstract class MyThread extends Thread {
        private static EnvManager envManager = null;
        private final Map<Class<? extends MyError>, ErrorHandler<? extends MyError>> errorHandlers = new HashMap<>();
        private boolean ignoreErrs = false;

        /**
         * Instantiates a new My thread.
         */
        public MyThread() {
            threads.add(this);

            setDaemon(false);
        }


        /**
         * Current thread.
         * will only execute code if inside a MyThread
         *
         * @param run the run
         */
        public static void currentThread(Callback<MyThread> run) {
            if (Thread.currentThread() instanceof MyThread myThread) {
                run.callback(myThread);
            }
        }

        public static void setEnvManager(EnvManager manager) {

            envManager = manager;

        }

        /**
         * Add handler.
         *
         * @param <E>      the type parameter
         * @param errClass the err class
         * @param onErr    the on err
         */
        public <E extends MyError> void addHandler(Class<E> errClass, ErrorHandler<E> onErr) {
            errorHandlers.put(errClass, onErr);
        }

        /**
         * Differ errs my thread.
         *
         * @return the my thread
         */
        protected MyThread differErrs() {
            return this;
        }

        /**
         * Stop run.
         */
        public void stopRun() {
            this.interrupt();
        }

        /**
         * Shleep.
         *
         * @param ms the ms
         */
        protected void shleep(int ms) {
            try {
                sleep(ms);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public final void run() {
            try {
                try {
                    handledRun();
                } catch (Throwable t) {
                    if (ignoreErrs)
                        return;
                    throw t;
                }
            } catch (MyError e) {
                Class<?> c = findClosestHandler(e.getClass());
                if (c != null && errorHandlers.containsKey(c)) {
                    ErrorHandler<?> handler = errorHandlers.get(c);
                    handler.handle(e);
                    envManager.handledErr(e);
                } else {
                    envManager.criticalErr(e);
                }
            } catch (Throwable t) {
                envManager.criticalErr(new MyError(t));
            }
        }

        /**
         * Ignore errs.
         */
        public void ignoreErrs() {
            ignoreErrs = true;
        }

        /**
         * Reactivate errs.
         */
        public void reactivateErrs() {
            ignoreErrs = false;
        }

        /**
         * Handled run.
         *
         * @throws Throwable the throwable
         */
        protected abstract void handledRun() throws Throwable;

        private Class<?> findClosestHandler(Class<?> clazz) {
            if (errorHandlers.containsKey(clazz)) {
                return clazz;
            }
            if (MyError.class.isAssignableFrom(clazz.getSuperclass())) {
                return findClosestHandler(clazz.getSuperclass());
            }
            return null;
        }
    }

    /**
     * The type Handled thread.
     */
    public static class HandledThread extends MyThread {

        private ThrowingRunnable runnable;

        /**
         * Instantiates a new Handled thread.
         */
        public HandledThread() {
            this(null);
        }

        /**
         * Instantiates a new Handled thread.
         *
         * @param runnable the runnable
         */
        public HandledThread(ThrowingRunnable runnable) {
            this.runnable = runnable;
        }

        /**
         * Run in handled thread handled thread.
         *
         * @param runnable the runnable
         * @return the handled thread
         */
        public static HandledThread runInHandledThread(ThrowingRunnable runnable) {
            HandledThread thread = new HandledThread(runnable);
            thread.start();
            return thread;
        }

        /**
         * Sets runnable.
         *
         * @param runnable the runnable
         */
        public void setRunnable(ThrowingRunnable runnable) {
            this.runnable = runnable;
        }

        @Override
        protected void handledRun() throws Throwable {
            if (runnable != null)
                this.runnable.run();
        }

    }
}
