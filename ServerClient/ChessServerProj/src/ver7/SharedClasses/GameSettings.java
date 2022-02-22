package ver7.SharedClasses;

import ver7.SharedClasses.GameSetup.AiParameters;

import java.io.Serializable;
import java.util.Objects;

public class GameSettings implements Serializable {
    public static final GameSettings EXAMPLE = new GameSettings(PlayerColor.WHITE, TimeFormat.BULLET, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", AiParameters.EZ_MY_AI, GameType.RESUME);

    private PlayerColor playerToMove;
    private TimeFormat timeFormat;
    private String fen = null;
    private String joiningToGameID = null;
    private String resumingGameID = null;
    private AiParameters aiParameters;
    private GameType gameType;

    public GameSettings(GameSettings other) {
        this.playerToMove = other.playerToMove;
        this.timeFormat = new TimeFormat(other.timeFormat);
        this.fen = other.fen;
        this.joiningToGameID = other.joiningToGameID;
        this.resumingGameID = other.resumingGameID;
        this.aiParameters = new AiParameters(other.aiParameters);
        this.gameType = other.gameType;
    }

    public GameSettings(PlayerColor playerToMove, TimeFormat timeFormat, AiParameters aiParameters, GameType gameType) {
        this(playerToMove, timeFormat, null, aiParameters, gameType);
    }

    public GameSettings(PlayerColor playerToMove, TimeFormat timeFormat, String fen, AiParameters aiParameters, GameType gameType) {
        this.playerToMove = playerToMove;
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

    public PlayerColor getPlayerToMove() {
        return playerToMove;
    }

    public void setPlayerToMove(PlayerColor playerToMove) {
        this.playerToMove = playerToMove;
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
        return playerToMove == that.playerToMove && Objects.equals(timeFormat, that.timeFormat) && Objects.equals(fen, that.fen) && Objects.equals(joiningToGameID, that.joiningToGameID) && Objects.equals(resumingGameID, that.resumingGameID) && Objects.equals(aiParameters, that.aiParameters) && gameType == that.gameType;
    }

    @Override
    public String toString() {
        return "GameSettings{" +
                "playerColor=" + playerToMove +
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
