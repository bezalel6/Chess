package ver13.SharedClasses.evaluation;

import ver13.SharedClasses.Location;
import ver13.SharedClasses.PlayerColor;
import ver13.SharedClasses.Utils.StrUtils;

import java.io.Serializable;

public class GameStatus implements Serializable {

    private final PlayerColor winningPlayerColor;
    private final SpecificStatus specificStatus;
    private int depth = -1;
    private Location checkedKingLoc = null;
    private GameStatusType gameStatusType;


    private GameStatus(SpecificStatus specificStatus) {
        this(null, specificStatus);
    }

    private GameStatus(PlayerColor winningPlayerColor, SpecificStatus specificStatus) {
        this.specificStatus = specificStatus;
        this.winningPlayerColor = winningPlayerColor;
        this.gameStatusType = specificStatus.gameStatusType;
    }

    public static GameStatus checkmate(PlayerColor winningPlayerColor, Location matedKing) {
        return new GameStatus(winningPlayerColor, SpecificStatus.Checkmate) {{
            setCheckedKingLoc(matedKing);
        }};
    }

    public static GameStatus gameGoesOn() {
        return new GameStatus(SpecificStatus.GameGoesOn);
    }

    public static GameStatus stalemate() {
        return new GameStatus(SpecificStatus.Stalemate);
    }

    public static GameStatus fiftyMoveRule() {
        return new GameStatus(SpecificStatus.FiftyMoveRule);
    }

    public static GameStatus serverStoppedGame() {
        return new GameStatus(SpecificStatus.ServerStoppedGame);
    }

    public static GameStatus threeFoldRepetition() {
        return new GameStatus(SpecificStatus.ThreeFoldRepetition);
    }

    public static GameStatus insufficientMaterial() {
        return new GameStatus(SpecificStatus.InsufficientMaterial);
    }

    public static GameStatus playerDisconnected(PlayerColor disconnectedPlayer) {
        return new GameStatus(disconnectedPlayer.getOpponent(), SpecificStatus.PlayerDisconnected);
    }

    public static GameStatus timedOut(PlayerColor timedOutPlayer) {
        return new GameStatus(timedOutPlayer.getOpponent(), SpecificStatus.TimedOut);
    }

    public static GameStatus playerResigned(PlayerColor resignedPlayer) {
        return new GameStatus(resignedPlayer.getOpponent(), SpecificStatus.Resignation);
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
        return winning + specificStatus + (isGameOver() && depth != -1 ? " In " + depth : "");
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

    public SpecificStatus getSpecificStatus() {
        return specificStatus;
    }

    public enum SpecificStatus {
        Checkmate,
        TimedOut,
        Resignation,
        GameGoesOn(GameStatusType.GAME_GOES_ON),
        ThreeFoldRepetition(GameStatusType.TIE),
        Stalemate(GameStatusType.TIE),
        InsufficientMaterial(GameStatusType.TIE),
        FiftyMoveRule(GameStatusType.TIE),
        PlayerDisconnected(GameStatusType.UNFINISHED),
        ServerStoppedGame(GameStatusType.UNFINISHED);
        public final GameStatusType gameStatusType;

        SpecificStatus() {
            this(GameStatusType.WIN_OR_LOSS);
        }

        SpecificStatus(GameStatusType gameStatusType) {
            this.gameStatusType = gameStatusType;
        }

        @Override
        public String toString() {
            return StrUtils.format(name());
        }
    }

    public enum GameStatusType implements Serializable {
        TIE("½½"), CHECK("+"), GAME_GOES_ON(""), WIN_OR_LOSS("#"), UNFINISHED("...");

        //        public static final GameStatusType[] GAME_OVERS = {TIE, WIN_OR_LOSS};
        public final String annotation;

        GameStatusType(String annotation) {
            this.annotation = annotation;
        }

        public boolean isGameOver() {
            return this == TIE || this == WIN_OR_LOSS || this == UNFINISHED;
        }
    }
}