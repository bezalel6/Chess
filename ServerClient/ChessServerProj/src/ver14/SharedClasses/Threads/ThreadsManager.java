package ver14.SharedClasses.Threads;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorHandler;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorManager;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Threads.ErrorHandling.ThrowingRunnable;

import java.util.ArrayList;

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

//        if (err != null)
//            throw err;
        if (err != null && ErrorHandler.canThrow())
            ErrorManager.handle(err);
    }

    public static MyThread createThread(ThrowingRunnable runnable, boolean start) {
        MyThread thread = new HandledThread(runnable);
        if (start)
            thread.start();
        return thread;
    }


    public static abstract class MyThread extends Thread {


        public MyThread() {
            threads.add(this);

            setDaemon(false);
        }

        protected <E extends MyError> void addHandler(Class<E> errClass, Callback<E> onErr) {

        }

        public void stopRun() {
            ErrorHandler.ignore(this::interrupt);
        }


        @Override
        public final void run() {
            handleErrors(this::handledRun);
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
