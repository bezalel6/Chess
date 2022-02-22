package ver6.SharedClasses;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class TimeFormat implements Serializable {
    public static final TimeFormat NORMAl = new TimeFormat(TimeUnit.MINUTES.toMillis(10));
    public static final TimeFormat BULLET = new TimeFormat(TimeUnit.SECONDS.toMillis(30));
    public static final TimeFormat[] PRESETS = {NORMAl, BULLET};
    public static final int numOfFields = 2;
    public final long timeInMillis;
    public final long incrementInMillis;

    public TimeFormat(long timeInMillis) {
        this(timeInMillis, 0);
    }

    public TimeFormat(long timeInMillis, long incrementInMillis) {
        this.timeInMillis = timeInMillis;
        this.incrementInMillis = incrementInMillis;
    }

    public TimeFormat(TimeFormat other) {
        this(other.timeInMillis, other.incrementInMillis);
    }

    @Override
    public String toString() {
        return "TimeFormat{" +
                "timeInMillis=" + timeInMillis +
                ", incrementInMillis=" + incrementInMillis +
                '}';
    }
}


