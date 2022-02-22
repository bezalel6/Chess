package ver5.SharedClasses;

import ver5.SharedClasses.GameSetup.AiParameters;

import java.io.Serializable;
import java.util.Objects;

public class GameSettings implements Serializable {
    public static final GameSettings EXAMPLE = new GameSettings(PlayerColor.WHITE, TimeFormat.BULLET, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", AiParameters.EZ_MY_AI, GameType.RESUME);

    private PlayerColor playerColor;
    private TimeFormat timeFormat;
    private String fen = null;
    private String joiningToGameID = null;
    private String resumingGameID = null;
    private AiParameters aiParameters;
    private GameType gameType;

    public GameSettings(GameSettings other) {
        this(other.playerColor, new TimeFormat(other.timeFormat), new AiParameters(other.aiParameters), other.gameType);
    }

    public GameSettings(PlayerColor playerColor, TimeFormat timeFormat, AiParameters aiParameters, GameType gameType) {
        this(playerColor, timeFormat, null, aiParameters, gameType);
    }

    public GameSettings(PlayerColor playerColor, TimeFormat timeFormat, String fen, AiParameters aiParameters, GameType gameType) {
        this.playerColor = playerColor;
        this.timeFormat = timeFormat;
        this.fen = fen;
        this.aiParameters = aiParameters;
        this.gameType = gameType;
    }

    public GameSettings() {
        aiParameters = new AiParameters();
    }


    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public String getResumingGameID() {
        return resumingGameID;
    }

    public void setResumingGameID(String resumingGameID) {
        this.resumingGameID = resumingGameID;
    }

    public boolean isVsAi() {
        return aiParameters != null;
    }

    public AiParameters getAiParameters() {
        return aiParameters;
    }

    public void setAiParameters(AiParameters aiParameters) {
        this.aiParameters = aiParameters;
    }

    public String getJoiningToGameID() {
        return joiningToGameID;
    }

    public void setJoiningToGameID(String joiningToGameID) {
        this.joiningToGameID = joiningToGameID;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(TimeFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameSettings that = (GameSettings) o;
        return playerColor == that.playerColor && Objects.equals(timeFormat, that.timeFormat) && Objects.equals(fen, that.fen) && Objects.equals(joiningToGameID, that.joiningToGameID) && Objects.equals(resumingGameID, that.resumingGameID) && Objects.equals(aiParameters, that.aiParameters) && gameType == that.gameType;
    }

    @Override
    public String toString() {
        return "GameSettings{" +
                "playerColor=" + playerColor +
                ", timeFormat=" + timeFormat +
                ", fen='" + fen + '\'' +
                ", joiningTo=" + joiningToGameID +
                ", resuming=" + resumingGameID +
                ", aiParameters=" + aiParameters +
                ", gameType=" + gameType +
                '}';
    }

    public enum GameType {
        JOIN_EXISTING, RESUME, CREATE_NEW;

    }
}
