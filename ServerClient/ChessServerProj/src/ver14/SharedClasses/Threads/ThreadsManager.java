package ver14.SharedClasses.Threads;

import ver14.SharedClasses.Threads.ErrorHandling.*;

import java.util.ArrayList;

public class ThreadsManager {
    private final static ArrayList<Thread> threads;


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
        ErrorManager.setHandler(MyError.ErrorType.Model, e -> {
            System.out.println("hmmm what a curious thing this is");
        });
        handleErrors(() -> {
            MyError err = new MyError(MyError.ErrorType.Model);
            err.addContext(new ErrorContext() {
                @Override
                public MyError.ContextType contextType() {
                    return MyError.ContextType.Game;
                }

                @Override
                public String toString() {
                    return "$classname{}";
                }
            });
            throw err;
        }).run();
    }

    public static Runnable handleErrors(ThrowingRunnable runnable) {
        return () -> {
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
            if (err != null)
                ErrorManager.handle(err);
        };
    }

    public static Thread createThread(Runnable runnable, boolean start) {
        Thread thread = new Thread(runnable);
        threads.add(thread);
        if (start)
            thread.start();
        return thread;
    }


    public static abstract class MyThread extends Thread {
        public MyThread() {
            threads.add(this);
        }

        @Override
        public final void run() {
            handleErrors(this::handledRun).run();
        }

        protected abstract void handledRun() throws Throwable;
    }

    public static class HandledThread extends MyThread {

        private final ThrowingRunnable runnable;

        public HandledThread(ThrowingRunnable runnable) {
            this.runnable = runnable;
        }

        public static HandledThread run(ThrowingRunnable runnable) {
            HandledThread thread = new HandledThread(runnable);
            thread.start();
            return thread;
        }

        @Override
        protected void handledRun() throws Throwable {
            this.runnable.run();
        }
    }
}
