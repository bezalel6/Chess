package ver14.SharedClasses;

import java.io.Serializable;
import java.util.Arrays;

public class GameTime implements Serializable {
    private static final int delay = 100;
    private final RunningTime[] gameTime;
    private PlayerColor currentlyRunning = null;

    public GameTime(GameTime other) {
        this.gameTime = other.gameTime;
        this.currentlyRunning = other.currentlyRunning;
    }

    public GameTime(TimeFormat... timeFormats) {
        gameTime = new RunningTime[PlayerColor.NUM_OF_PLAYERS];
        if (timeFormats.length == 1) {
            for (int i = 0; i < PlayerColor.NUM_OF_PLAYERS; i++) {
                gameTime[i] = new RunningTime(timeFormats[0]);
            }
        } else if (timeFormats.length == PlayerColor.NUM_OF_PLAYERS) {
            for (int i = 0; i < timeFormats.length; i++) {
                gameTime[i] = new RunningTime(timeFormats[i]);
            }
        }

    }

    public void startRunning(PlayerColor playerColor) {
        getRunningTime(playerColor).startRunning();
        this.currentlyRunning = playerColor;
    }

    public RunningTime getRunningTime(PlayerColor playerColor) {
        return gameTime[playerColor.ordinal()];
    }

    public void stopRunning(PlayerColor playerColor) {
        getRunningTime(playerColor).stopRunning();
        this.currentlyRunning = null;
    }

    public GameTime clean() {
//        return this;
        return new GameTime(this);
    }

    public void syncMe(GameTime other) {
        System.arraycopy(other.gameTime, 0, gameTime, 0, other.gameTime.length);
    }

    public long getTimeLeft(PlayerColor playerColor) {
        return getRunningTime(playerColor).getTimeInMillis();
    }

    public PlayerColor getCurrentlyRunning() {
        return currentlyRunning;
    }

    @Override
    public String toString() {
        return "GameTime{" +
                "gameTime=" + Arrays.toString(gameTime) +
                ", currentlyRunning=" + currentlyRunning +
                '}';
    }

    static public class RunningTime implements Serializable {
        private final long incrementInMillis;
        private long timeInMillis;
        private long lastStarted;

        public RunningTime(TimeFormat timeFormat) {
            this.timeInMillis = timeFormat.timeInMillis;
            this.incrementInMillis = timeFormat.incrementInMillis;
        }

        public void startRunning() {
            lastStarted = System.currentTimeMillis();
        }

        public void stopRunning() {
            timeInMillis -= System.currentTimeMillis() - lastStarted + incrementInMillis;
        }

        public long getTimeInMillis() {
            return timeInMillis;
        }

        public boolean didRunOut() {
            return timeInMillis <= 0;
        }

        @Override
        public String toString() {
            return "RunningTime{" +
                    "incrementInMillis=" + incrementInMillis +
                    ", timeInMillis=" + timeInMillis +
                    '}';
        }
    }
}

