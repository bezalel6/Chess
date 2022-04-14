package ver14.SharedClasses.Threads;

import ver14.SharedClasses.Threads.ErrorHandling.EnvManager;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorHandler;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Threads.ErrorHandling.ThrowingRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ThreadsManager {
    private final static ArrayList<MyThread> threads;


    static {
        threads = new ArrayList<>();
    }


    public static void stopAll() {
        try {
            threads.forEach(MyThread::stopRun);
        } catch (Exception e) {
            System.out.println("stopping threads");
            System.out.println(e.getClass());
        }
    }

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
        if (err != null)
            throw err;
    }

    public static MyThread createThread(ThrowingRunnable runnable, boolean start) {
        MyThread thread = new HandledThread(runnable);
        if (start)
            thread.start();
        return thread;
    }


    public static abstract class MyThread extends Thread {
        private static EnvManager envManager = null;
        private final Map<Class<? extends MyError>, ErrorHandler<? extends MyError>> errorHandlers = new HashMap<>();


        public MyThread() {
            threads.add(this);

            setDaemon(false);
        }

        public static void setEnvManager(EnvManager manager) {

            envManager = manager;

        }

        protected <E extends MyError> void addHandler(Class<E> errClass, ErrorHandler<E> onErr) {
            errorHandlers.put(errClass, onErr);
        }

        protected MyThread differErrs() {
            return this;
        }


        public void stopRun() {
            this.interrupt();
//            ErrorHandler.ignore(this::interrupt);
        }


        @Override
        public final void run() {
            try {
                handledRun();
            } catch (MyError e) {
                if (errorHandlers.containsKey(e.getClass())) {
                    ErrorHandler<?> handler = errorHandlers.get(e.getClass());
                    handler.handle(e);
                } else {
                    envManager.criticalErr(e);
                }
            } catch (Throwable t) {
                envManager.criticalErr(new MyError(t));
            }
        }

        protected abstract void handledRun() throws Throwable;
    }

    public static class HandledThread extends MyThread {

        private ThrowingRunnable runnable;

        public HandledThread() {
            this(null);
        }

        public HandledThread(ThrowingRunnable runnable) {
            this.runnable = runnable;
        }

        public static HandledThread runInHandledThread(ThrowingRunnable runnable) {
            HandledThread thread = new HandledThread(runnable);
            thread.start();
            return thread;
        }

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
