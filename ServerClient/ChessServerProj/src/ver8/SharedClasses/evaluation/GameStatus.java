package ver8.SharedClasses.evaluation;

import ver8.SharedClasses.Location;
import ver8.SharedClasses.PlayerColor;
import ver8.SharedClasses.StrUtils;

import java.io.Serializable;

public class GameStatus implements Serializable {

    private final PlayerColor winningPlayerColor;
    private final String gameStatusName;
    private int depth = -1;
    private GameStatusType gameStatusType;
    private Location checkedKingLoc = null;

    private GameStatus(GameStatusType gameStatusType, PlayerColor winningPlayerColor) {
        this(gameStatusType, winningPlayerColor, "");
    }

    private GameStatus(GameStatusType gameStatusType, PlayerColor winningPlayerColor, String gameStatusName) {
        this.gameStatusType = gameStatusType;
        this.winningPlayerColor = winningPlayerColor;
        this.gameStatusName = StrUtils.format(gameStatusName);
    }

    public static GameStatus checkmate(PlayerColor winningPlayerColor, Location matedKing) {
        return new GameStatus(GameStatusType.WIN_OR_LOSS, winningPlayerColor, "Checkmate") {{
            setCheckedKingLoc(matedKing);
        }};
    }

    public static GameStatus gameGoesOn() {
        return new GameStatus(GameStatusType.GAME_GOES_ON, null);
    }

    public static GameStatus stalemate() {
        return new GameStatus(GameStatusType.TIE, null, "Stalemate");
    }

    public static GameStatus fiftyMoveRule() {
        return new GameStatus(GameStatusType.TIE, null, "Fifty Move Rule");
    }

    public static GameStatus serverStoppedGame() {
        return new GameStatus(GameStatusType.TIE, null, "Server stopped game");
    }

    public static GameStatus threeFoldRepetition() {
        return new GameStatus(GameStatusType.TIE, null, "Three Fold Repetition");
    }

    public static GameStatus insufficientMaterial() {
        return new GameStatus(GameStatusType.TIE, null, "Insufficient Material");
    }

    public static GameStatus playerDisconnected(PlayerColor disconnectedPlayer) {
        return new GameStatus(GameStatusType.UNFINISHED, disconnectedPlayer, "Other Player Disconnected");
    }

    public static GameStatus timedOut(PlayerColor timedOutPlayer) {
        return new GameStatus(GameStatusType.WIN_OR_LOSS, timedOutPlayer.getOpponent(), "On Time");
    }

    public static GameStatus playerResigned(PlayerColor resignedPlayer) {
        return new GameStatus(GameStatusType.WIN_OR_LOSS, resignedPlayer.getOpponent(), "resignation");
    }

    public Location getCheckedKingLoc() {
        return checkedKingLoc;
    }

    void setCheckedKingLoc(Location checkedKingLoc) {
        this.checkedKingLoc = checkedKingLoc;
    }

    public PlayerColor getWinningColor() {
        return winningPlayerColor;
    }

    public GameStatusType getGameStatusType() {
        return gameStatusType;
    }

    public void setInCheck(Location checkedKingLoc) {
        this.gameStatusType = GameStatusType.CHECK;
        setCheckedKingLoc(checkedKingLoc);
    }

    public boolean isCheck() {
        return gameStatusType == GameStatusType.CHECK || checkedKingLoc != null;
    }

    public String getDetailedStr() {
        String winning = "";
        if (winningPlayerColor != null) {
            winning = winningPlayerColor.getName() + " Won By ";
        }
        return winning + gameStatusName + (isGameOver() && depth != -1 ? " In " + depth : "");
    }

    public boolean isGameOver() {
        return gameStatusType.isGameOver();
    }

    @Override
    public String toString() {
        return gameStatusType.annotation;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public enum GameStatusType implements Serializable {
        TIE("½½"), CHECK("+"), GAME_GOES_ON(""), WIN_OR_LOSS("#"), UNFINISHED("...");

        public static final GameStatusType[] GAME_OVERS = {TIE, WIN_OR_LOSS};
        public final String annotation;

        GameStatusType(String annotation) {
            this.annotation = annotation;
        }

        public boolean isGameOver() {
            return this == TIE || this == WIN_OR_LOSS || this == UNFINISHED;
        }
    }
}