package ver14.SharedClasses.Threads;

import ver14.SharedClasses.Threads.ErrorHandling.*;

import java.util.ArrayList;

public class ThreadsManager {
    private final static ArrayList<MyThread> threads;


    static {
        threads = new ArrayList<>();
    }

    public static void main(String[] args) {
        ErrorManager.setEnvManager(new EnvManager() {
            @Override
            public void handledErr(MyError err) {
                System.out.println(err + "");
            }

            @Override
            public void criticalErr(MyError err) {
                System.err.println(err + " *panik*");
            }
        });
        ErrorManager.setHandler(ErrorType.Model, e -> {
            System.out.println("hmmm what a curious thing this is");
        });
        handleErrors(() -> {
            MyError err = new MyError(ErrorType.Model);
            err.addContext(new ErrorContext() {
                @Override
                public ContextType contextType() {
                    return ContextType.Game;
                }

                @Override
                public String toString() {
                    return "$classname{}";
                }
            });
            throw err;
        });
    }

    public static void handleErrors(ThrowingRunnable runnable) {
        MyError err = null;
        try {
            runnable.run();
        } catch (MyError e) {
            err = e;
        } catch (AssertionError assertionError) {
            throw assertionError;
        } catch (Throwable t) {
            err = new MyError(t);
        }
//        if (err != null)
//            throw err;
        if (err != null)
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

        public static void closeAll() {
            threads.forEach(Thread::interrupt);
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
