package ver5.SharedClasses;

import javax.swing.*;
import java.io.Serializable;
import java.util.Arrays;

public class GameTime implements Serializable {
    private static final int delay = 100;
    private final RunningTime[] gameTime;
    private final Timer timer;
    private PlayerColor currentlyRunning = null;

    public GameTime(GameTime other) {
        this.gameTime = other.gameTime;
        this.timer = other.timer;
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
        } else throw new Error();

        timer = new Timer(delay, e -> timerPulse());
    }

    private void timerPulse() {
        if (currentlyRunning != null) {
            getRunningTime(currentlyRunning).dec(delay);
            if (getRunningTime(currentlyRunning).didRunOut()) {
                timedOut();
            }
        }
    }

    public RunningTime getRunningTime(PlayerColor playerColor) {
        return gameTime[playerColor.asInt()];
    }

    public void timedOut() {

    }

    public GameTime clean() {
        return new GameTime(this);
    }

    public void startTimer() {
        timer.start();
    }

    public void stopTimer() {
        timer.stop();
    }

    public void syncMe(GameTime other) {
        System.arraycopy(other.gameTime, 0, gameTime, 0, other.gameTime.length);
        this.currentlyRunning = other.currentlyRunning;
    }

    public long getTimeLeft(PlayerColor playerColor) {
        return getRunningTime(playerColor).getTimeInMillis();
    }

    public void runTime(PlayerColor playerColor) {
        currentlyRunning = playerColor;
    }

    @Override
    public String toString() {
        return "GameTime{" +
                "gameTime=" + Arrays.toString(gameTime) +
                ", timer=" + timer +
                ", currentlyRunning=" + currentlyRunning +
                '}';
    }
}

class RunningTime implements Serializable {
    private final long incrementInMillis;
    private long timeInMillis;

    public RunningTime(TimeFormat timeFormat) {
        this.timeInMillis = timeFormat.timeInMillis;
        this.incrementInMillis = timeFormat.incrementInMillis;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public boolean didRunOut() {
        return timeInMillis <= 0;
    }

    public void dec(long amountInMillis) {
        timeInMillis -= amountInMillis + incrementInMillis;
    }

    @Override
    public String toString() {
        return "RunningTime{" +
                "incrementInMillis=" + incrementInMillis +
                ", timeInMillis=" + timeInMillis +
                '}';
    }
}