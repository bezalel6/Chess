package ver14.SharedClasses.Threads;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Threads.ErrorHandling.EnvManager;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorHandler;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;

import java.util.HashMap;
import java.util.Map;


/**
 * The type My thread.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class MyThread extends Thread {
    /**
     * The constant envManager.
     */
    private static EnvManager envManager = null;
    /**
     * The Error handlers.
     */
    private final Map<Class<? extends MyError>, ErrorHandler<? extends MyError>> errorHandlers = new HashMap<>();
    /**
     * The Ignore errs.
     */
    private boolean ignoreErrs = false;
    /**
     * The Thread status.
     */
    private ThreadStatus threadStatus = ThreadStatus.NOT_STARTED;


    /**
     * Instantiates a new My thread.
     */
    public MyThread() {
        ThreadsManager.addThread(this);
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

    /**
     * Sets env manager.
     *
     * @param manager the manager
     */
    public static void setEnvManager(EnvManager manager) {

        envManager = manager;

    }

    /**
     * Is ignore errs boolean.
     *
     * @return the boolean
     */
    public boolean isIgnoreErrs() {
        return ignoreErrs;
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
     * Stop run.
     */
    public void stopRun() {
        if (threadStatus == ThreadStatus.RUNNING) {
            this.interrupt();
        }
    }

//        /**
//         * Differ errs my thread.
//         *
//         * @return the my thread
//         */
//        protected MyThread differErrs() {
//            return this;
//        }


    /**
     * Run.
     */
    @Override
    public final void run() {
        threadStatus = threadStatus.next();
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
        } finally {
            threadStatus = threadStatus.next();
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

    /**
     * Find closest handler class.
     *
     * @param clazz the clazz
     * @return the class
     */
    private Class<?> findClosestHandler(Class<?> clazz) {
        if (errorHandlers.containsKey(clazz)) {
            return clazz;
        }
        if (MyError.class.isAssignableFrom(clazz.getSuperclass())) {
            return findClosestHandler(clazz.getSuperclass());
        }
        return null;
    }

    /**
     * Thread status.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public enum ThreadStatus {
        /**
         * The Not started.
         */
        NOT_STARTED {
            @Override
            public ThreadStatus next() {
                return RUNNING;
            }
        },
        /**
         * The Running.
         */
        RUNNING {
            @Override
            public ThreadStatus next() {
                return DONE;
            }
        },
        /**
         * The Done.
         */
        DONE {
            @Override
            public ThreadStatus next() {
                return null;
            }
        };

        /**
         * Next thread status.
         *
         * @return the thread status
         */
        public abstract ThreadStatus next();
    }
}
