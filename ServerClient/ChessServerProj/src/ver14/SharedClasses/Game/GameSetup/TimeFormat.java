package ver14.SharedClasses.Game.GameSetup;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/*
 * TimeFormat
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * TimeFormat -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * TimeFormat -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class TimeFormat implements Serializable {
    public static final TimeFormat RAPID = new TimeFormat(TimeUnit.MINUTES.toMillis(10));
    public static final TimeFormat ULTRA_BULLET = new TimeFormat(TimeUnit.SECONDS.toMillis(2));
    public static final TimeFormat BULLET = new TimeFormat(TimeUnit.MINUTES.toMillis(1));
    public static final TimeFormat BULLET2 = new TimeFormat(TimeUnit.MINUTES.toMillis(2));
    public static final TimeFormat[] PRESETS = {RAPID, ULTRA_BULLET, BULLET, BULLET2};
    public static final int numOfFields = 2;
    public final long timeInMillis;

    public TimeFormat(TimeFormat other) {
        this(other.timeInMillis);
    }

    public TimeFormat(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }


    @Override
    public String toString() {
        return "TimeFormat{" +
                "timeInMillis=" + timeInMillis +
                '}';
    }
}


