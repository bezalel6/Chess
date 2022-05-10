package ver14.SharedClasses.Game.GameSetup;

import ver14.SharedClasses.Game.PlayerColor;

import java.io.Serializable;
import java.util.Arrays;


/**
 * represents the current state of both player's time.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class GameTime implements Serializable {
    /**
     * The Game time.
     */
    private final long[] gameTime;
    /**
     * The Time formats.
     */
    private final TimeFormat[] timeFormats;
    /**
     * The Currently running.
     */
    private PlayerColor currentlyRunning = null;
    private long lastStart = 0;

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
     * Instantiates a new Game time with the formats for both players.
     * possible inputs: [{@link TimeFormat}] will set both players' times to that time.
     * [{@link TimeFormat}, {@link TimeFormat}] will set white player's time to the first one and black's to the second.
     *
     * @param timeFormats the time formats
     */
    public GameTime(TimeFormat... timeFormats) {
        assert timeFormats.length > 0;
        this.timeFormats = timeFormats;
        gameTime = new long[PlayerColor.NUM_OF_PLAYERS];
        if (timeFormats.length == 1) {
            for (int i = 0; i < PlayerColor.NUM_OF_PLAYERS; i++) {
                gameTime[i] = (timeFormats[0].timeInMillis);
            }
        } else if (timeFormats.length == PlayerColor.NUM_OF_PLAYERS) {
            for (int i = 0; i < timeFormats.length; i++) {
                gameTime[i] = (timeFormats[i].timeInMillis);
            }
        }

    }

    /**
     * Start running a player's clock.
     *
     * @param playerColor the player color
     */
    public synchronized void startRunning(PlayerColor playerColor) {
        lastStart = System.currentTimeMillis();
        this.currentlyRunning = playerColor;
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
     * Gets the time left for a player.
     *
     * @param playerColor the player color
     * @return the time left in milliseconds
     */
    public long getTimeLeft(PlayerColor playerColor) {
        var f = getTimeFormat(playerColor);
        return playerColor == currentlyRunning ? System.currentTimeMillis() - lastStart : f.timeInMillis;
    }

    /**
     * Gets the time format for a player.
     *
     * @param clr the clr
     * @return the time format
     */
    public TimeFormat getTimeFormat(PlayerColor clr) {
        return timeFormats.length > 1 ? timeFormats[clr.asInt] : timeFormats[0];
    }

    /**
     * Gets the player that is currently running.
     *
     * @return the player whose time is currently running
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

}

