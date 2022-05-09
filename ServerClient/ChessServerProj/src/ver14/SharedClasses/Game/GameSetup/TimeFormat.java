package ver14.SharedClasses.Game.GameSetup;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;


/**
 * represents a player's Time format. how much time does he have for each move.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class TimeFormat implements Serializable {
    /**
     * The constant RAPID.
     */
    public static final TimeFormat RAPID = new TimeFormat(TimeUnit.MINUTES.toMillis(10));
    /**
     * The constant ULTRA_BULLET.
     */
    public static final TimeFormat ULTRA_BULLET = new TimeFormat(TimeUnit.SECONDS.toMillis(2));
    /**
     * The constant BULLET.
     */
    public static final TimeFormat BULLET = new TimeFormat(TimeUnit.MINUTES.toMillis(1));
    /**
     * The constant BULLET2.
     */
    public static final TimeFormat BULLET2 = new TimeFormat(TimeUnit.MINUTES.toMillis(2));
    /**
     * The constant PRESETS.
     */
    public static final TimeFormat[] PRESETS = {RAPID, ULTRA_BULLET, BULLET, BULLET2};
    /**
     * The constant numOfFields.
     */
    public static final int numOfFields = 2;
    /**
     * The Time in millis.
     */
    public final long timeInMillis;

    /**
     * Instantiates a new Time format.
     *
     * @param other the other
     */
    public TimeFormat(TimeFormat other) {
        this(other.timeInMillis);
    }

    /**
     * Instantiates a new Time format.
     *
     * @param timeInMillis the time in millis
     */
    public TimeFormat(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }


    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "TimeFormat{" +
                "timeInMillis=" + timeInMillis +
                '}';
    }
}


