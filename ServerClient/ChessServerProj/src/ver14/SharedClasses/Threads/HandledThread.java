package ver14.SharedClasses.Threads;

import ver14.SharedClasses.Threads.ErrorHandling.ThrowingRunnable;


/**
 * represents a thread that can handle errors.
 */
public class HandledThread extends MyThread {

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
     * Run in a handled thread
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
