package ver14.SharedClasses.Game.GameSetup;

import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Misc.ParentOf;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;


/**
 * represents Game settings. the starting position, which {@link PlayerColor} turn is it to move, the {@link TimeFormat},
 * the {@link AISettings} and the {@link GameType}
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class GameSettings implements Serializable, ParentOf<TimeFormat> {

    @Serial
    private static final long serialVersionUID = 100L;
    /**
     * The Player to move.
     */
    private PlayerColor playerToMove;
    /**
     * The Time format.
     */
    private TimeFormat timeFormat;
    /**
     * The Fen.
     */
    private String fen = null;
    /**
     * The Game id.
     */
    private String gameID;
    /**
     * The Ai parameters.
     */
    private AISettings AISettings;
    /**
     * The Game type.
     */
    private GameType gameType;

    /**
     * Instantiates a new Game settings.
     *
     * @param other the other
     */
    public GameSettings(GameSettings other) {
        this.playerToMove = other.playerToMove;
        this.timeFormat = new TimeFormat(other.timeFormat);
        this.fen = other.fen;
        this.gameID = other.gameID;
        this.AISettings = new AISettings(other.AISettings);
        this.gameType = other.gameType;
    }

    /**
     * Instantiates a new Game settings.
     *
     * @param AISettings the ai parameters
     */
    public GameSettings(AISettings AISettings) {
        this(PlayerColor.NO_PLAYER, TimeFormat.ULTRA_BULLET, AISettings, GameType.CREATE_NEW);
    }

    /**
     * Instantiates a new Game settings.
     *
     * @param playerToMove the player to move
     * @param timeFormat   the time format
     * @param AISettings   the ai parameters
     * @param gameType     the game type
     */
    public GameSettings(PlayerColor playerToMove, TimeFormat timeFormat, AISettings AISettings, GameType gameType) {
        this(playerToMove, timeFormat, null, AISettings, gameType);
    }

    /**
     * Instantiates a new Game settings.
     *
     * @param playerToMove the player to move
     * @param timeFormat   the time format
     * @param fen          the fen
     * @param AISettings   the ai parameters
     * @param gameType     the game type
     */
    public GameSettings(PlayerColor playerToMove, TimeFormat timeFormat, String fen, AISettings AISettings, GameType gameType) {
        this.playerToMove = playerToMove;
        this.timeFormat = timeFormat;
        this.fen = fen;
        this.AISettings = AISettings;
        this.gameType = gameType;
    }

    /**
     * Instantiates a new Game settings.
     */
    public GameSettings() {
        AISettings = new AISettings();
    }


    /**
     * Gets fen.
     *
     * @return the fen
     */
    public String getFen() {
        return fen;
    }

    /**
     * Sets fen.
     *
     * @param fen the fen
     */
    public void setFen(String fen) {
        if (fen != null && fen.trim().equals("")) {
            fen = null;
        }
        this.fen = fen;
    }

    /**
     * Gets game id.
     *
     * @return the game id
     */
    public String getGameID() {
        return gameID;
    }

    /**
     * Sets game id.
     *
     * @param gameID the game id
     */
    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    /**
     * Is vs ai boolean.
     *
     * @return the boolean
     */
    public boolean isVsAi() {
        return AISettings != null && !AISettings.isEmpty();
    }

    /**
     * Gets ai parameters.
     *
     * @return the ai parameters
     */
    public AISettings getAISettings() {
        return AISettings;
    }

    /**
     * Sets ai parameters.
     *
     * @param AISettings the ai parameters
     */
    public void setAISettings(AISettings AISettings) {
        this.AISettings = AISettings;
    }


    /**
     * Gets game type.
     *
     * @return the game type
     */
    public GameType getGameType() {
        return gameType;
    }

    /**
     * Sets game type.
     *
     * @param gameType the game type
     */
    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    /**
     * Gets player to move.
     *
     * @return the player to move
     */
    public PlayerColor getPlayerToMove() {
        return playerToMove;
    }

    /**
     * Sets player to move.
     *
     * @param playerToMove the player to move
     */
    public void setPlayerToMove(PlayerColor playerToMove) {
        this.playerToMove = playerToMove;
    }

    /**
     * Init default 1 v ai.
     */
    public void initDefault1vAi() {
        initDefault1v1();
        setAISettings(new AISettings(ver14.SharedClasses.Game.GameSetup.AISettings.AiType.MyAi, new TimeFormat(3500)));
    }

    /**
     * Init default 1 v 1.
     */
    public void initDefault1v1() {
        setPlayerToMove(PlayerColor.WHITE);
        set(TimeFormat.RAPID);
        setGameType(GameType.CREATE_NEW);
        setAISettings(null);
        setFen(null);
    }

    /**
     * Sets time format.
     *
     * @param timeFormat the time format
     */
    @Override
    public void set(TimeFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    /**
     * Gets time format.
     *
     * @return the time format
     */
    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(playerToMove, timeFormat, fen, gameID, AISettings, gameType);
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameSettings that = (GameSettings) o;
        return playerToMove == that.playerToMove && Objects.equals(timeFormat, that.timeFormat) && Objects.equals(fen, that.fen) && Objects.equals(gameID, that.gameID) && Objects.equals(AISettings, that.AISettings) && gameType == that.gameType;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "GameSettings{" +
                "playerToMove=" + playerToMove +
                ", timeFormat=" + timeFormat +
                ", fen='" + fen + '\'' +
                ", gameID='" + gameID + '\'' +
                ", aiParameters=" + AISettings +
                ", gameType=" + gameType +
                '}';
    }

}
