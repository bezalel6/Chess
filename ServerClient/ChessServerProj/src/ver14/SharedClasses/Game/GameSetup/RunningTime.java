package ver14.SharedClasses.Game.GameSetup;

import java.io.Serializable;

/**
 * .
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class RunningTime implements Serializable {
    /**
     * The Time in millis.
     */
    private long timeInMillis;
    /**
     * The Last started.
     */
    private long lastStarted;

    /**
     * Instantiates a new Running time.
     *
     * @param timeFormat the time format
     */
    public RunningTime(TimeFormat timeFormat) {
        this.timeInMillis = timeFormat.timeInMillis;
    }

    /**
     * Start running.
     */
    public void startRunning() {
        lastStarted = System.currentTimeMillis();
    }

    /**
     * Stop running.
     */
    public void stopRunning() {
        timeInMillis -= System.currentTimeMillis() - lastStarted;
    }

    /**
     * Gets time in millis.
     *
     * @return the time in millis
     */
    public long getTimeInMillis() {
        return timeInMillis;
    }

    /**
     * Did run out boolean.
     *
     * @return the boolean
     */
    public boolean didRunOut() {
        return timeInMillis <= 0;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "RunningTime{" +
                ", timeInMillis=" + timeInMillis +
                '}';
    }
}
