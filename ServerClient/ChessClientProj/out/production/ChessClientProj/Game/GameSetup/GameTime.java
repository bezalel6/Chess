package ver14.SharedClasses.Game.GameSetup;

import ver14.SharedClasses.Game.PlayerColor;

import java.io.Serializable;
import java.util.Arrays;


/**
 * Game time.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class GameTime implements Serializable {
    /**
     * The constant delay.
     */
    private static final int delay = 100;
    /**
     * The Game time.
     */
    private final RunningTime[] gameTime;
    /**
     * The Time formats.
     */
    private final TimeFormat[] timeFormats;
    /**
     * The Currently running.
     */
    private PlayerColor currentlyRunning = null;

    /**
     * Instantiates a new Game time.
     *
     * @param other the other
     */
    public GameTime(GameTime other) {
        this.gameTime = other.gameTime;
        this.currentlyRunning = other.currentlyRunning;
        this.timeFormats = other.timeFormats;
    }

    /**
     * Instantiates a new Game time.
     *
     * @param timeFormats the time formats
     */
    public GameTime(TimeFormat... timeFormats) {
        this.timeFormats = timeFormats;
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

    /**
     * Gets time format.
     *
     * @param clr the clr
     * @return the time format
     */
    public TimeFormat getTimeFormat(PlayerColor clr) {
        return timeFormats.length > 1 ? timeFormats[clr.asInt] : timeFormats[0];
    }

    /**
     * Start running.
     *
     * @param playerColor the player color
     */
    public void startRunning(PlayerColor playerColor) {
        getRunningTime(playerColor).startRunning();
        this.currentlyRunning = playerColor;
    }

    /**
     * Gets running time.
     *
     * @param playerColor the player color
     * @return the running time
     */
    public RunningTime getRunningTime(PlayerColor playerColor) {
        return gameTime[playerColor.ordinal()];
    }

    /**
     * Stop running.
     *
     * @param playerColor the player color
     */
    public void stopRunning(PlayerColor playerColor) {
        getRunningTime(playerColor).stopRunning();
        this.currentlyRunning = null;
    }

    /**
     * Clean game time.
     *
     * @return the game time
     */
    public GameTime clean() {
//        return this;
        return new GameTime(this);
    }

    /**
     * Sync me.
     *
     * @param other the other
     */
    public void syncMe(GameTime other) {
        System.arraycopy(other.gameTime, 0, gameTime, 0, other.gameTime.length);
    }

    /**
     * Gets time left.
     *
     * @param playerColor the player color
     * @return the time left
     */
    public long getTimeLeft(PlayerColor playerColor) {
        return getRunningTime(playerColor).getTimeInMillis();
    }

    /**
     * Gets currently running.
     *
     * @return the currently running
     */
    public PlayerColor getCurrentlyRunning() {
        return currentlyRunning;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "GameTime{" +
                "gameTime=" + Arrays.toString(gameTime) +
                ", currentlyRunning=" + currentlyRunning +
                '}';
    }

    /**
     * Running time.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    static public class RunningTime implements Serializable {
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
}

