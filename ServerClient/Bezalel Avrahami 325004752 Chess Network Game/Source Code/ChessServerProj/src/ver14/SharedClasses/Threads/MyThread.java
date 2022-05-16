package ver14.SharedClasses.Threads;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Threads.ErrorHandling.EnvManager;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorHandler;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;

import java.util.HashMap;
import java.util.Map;


/**
 * represents My implementation of an error handling thread.
 * when an error is thrown inside a {@link MyThread}, a map of
 * error handlers is searched to find a handler set to deal with the type of error thrown.
 * if one is found, it is called. and the {@link EnvManager} will be notified of a
 * handled error.
 * if a handler is not found, the {@link EnvManager} will be notified that an unhandled error has occurred,
 * and he will then begin shutting down.
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
    private final Map<Class<? extends MyError>, ErrorHandler> errorHandlers = new HashMap<>();
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
     * Sets the manager of this environment.
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
     * Add an error handler.
     *
     * @param <E>      the type of errors this handler will handle
     * @param errClass the class of the error
     * @param onErr    the error handler
     */
    public <E extends MyError> void addHandler(Class<E> errClass, ErrorHandler onErr) {
        errorHandlers.put(errClass, onErr);
    }

    /**
     * Stop the run of this thread. if this thread's state is set to {@link ThreadStatus#RUNNING}
     * it will be interrupted.
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
                ErrorHandler handler = errorHandlers.get(c);
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
     * run this thread in a handled manner.
     *
     * @throws Throwable the error that might get thrown
     */
    protected abstract void handledRun() throws Throwable;

    /**
     * Find the closest handler set to the thrown error.
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
         * Not started.
         */
        NOT_STARTED {
            @Override
            public ThreadStatus next() {
                return RUNNING;
            }
        },
        /**
         * Running.
         */
        RUNNING {
            @Override
            public ThreadStatus next() {
                return DONE;
            }
        },
        /**
         * Done.
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
