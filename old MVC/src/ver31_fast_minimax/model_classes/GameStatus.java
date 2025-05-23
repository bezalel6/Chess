package ver31_fast_minimax.model_classes;

import ver31_fast_minimax.Player;

import static ver31_fast_minimax.model_classes.moves.MoveAnnotation.CHECK_ANN;
import static ver31_fast_minimax.model_classes.moves.MoveAnnotation.GAME_STATUS_ANNOTATIONS;

public class GameStatus {
    public static final int NUM_OF_GAME_OVERS = 8;
    public static final int CHECKMATE = 0;
    public static final int TIMED_OUT = 7;
    public static final int INSUFFICIENT_MATERIAL = 1;
    public static final int STALEMATE = 2;
    public static final int THREE_FOLD_REPETITION = 3;
    public static final int FIFTY_MOVE_RULE = 4;
    public static final int GAME_GOES_ON = 5;
    public static final int CHECK = 6;
    private static final String[] GAME_STATUS_STRS = initializeStatusStrs();
    private static final GameStatusType[] GAME_STATUS_TYPES = initializeStatusTypes();
    //    public static final int WIN_OR_LOSS = 0;
//    public static final int TIE = 1;
//    public static final int CHECK = 2;
//    public static final int GAME_GOES_ON = 3;
    private static final String WIN_STR = "Won", LOSS_STR = "Lost";
    private int specificGameStatus;
    private GameStatusType gameStatusType;
    private int player = -1;

    public GameStatus(int specificGameStatus, int player) {
        this.specificGameStatus = specificGameStatus;
        this.player = player;
        this.gameStatusType = GAME_STATUS_TYPES[specificGameStatus];
    }

    public GameStatus(int specificGameStatus) {
        this(specificGameStatus, Player.NO_PLAYER);
    }

    public GameStatus() {
        this(GAME_GOES_ON, Player.NO_PLAYER);
    }

    public GameStatus(GameStatus other) {
        this.gameStatusType = other.gameStatusType;
        this.specificGameStatus = other.specificGameStatus;
    }

    private static GameStatusType[] initializeStatusTypes() {
        GameStatusType[] ret = new GameStatusType[NUM_OF_GAME_OVERS];
        ret[CHECKMATE] = ret[TIMED_OUT] = GameStatusType.WIN_OR_LOSS;
        ret[INSUFFICIENT_MATERIAL] = ret[STALEMATE] = ret[THREE_FOLD_REPETITION] = ret[FIFTY_MOVE_RULE] = GameStatusType.TIE;
        ret[GAME_GOES_ON] = ret[CHECK] = GameStatusType.GAME_GOES_ON;
        return ret;
    }

    private static String[] initializeStatusStrs() {
        String[] ret = new String[NUM_OF_GAME_OVERS];
        ret[CHECKMATE] = "By Checkmate";
        ret[INSUFFICIENT_MATERIAL] = "Draw by Insufficient Material";
        ret[STALEMATE] = "Draw by Stalemate";
        ret[THREE_FOLD_REPETITION] = "Draw by Three Fold Repetition";
        ret[FIFTY_MOVE_RULE] = "Draw by Fifty Move Rule";
        ret[CHECK] = CHECK_ANN;
        ret[GAME_GOES_ON] = "";
        ret[TIMED_OUT] = "By Time Out";
        return ret;
    }

    public static String getGameStatusAnnotation(GameStatus status) {
        return GAME_STATUS_ANNOTATIONS[status.specificGameStatus];
    }

    public GameStatusType getGameStatusType() {
        return gameStatusType;
    }

    public void setGameStatusType(GameStatusType gameStatusType) {
        this.gameStatusType = gameStatusType;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public boolean isCheck() {
//        todo || is checkmate (always check. it might be mate!
        return gameStatusType == GameStatusType.CHECK || gameStatusType == GameStatusType.WIN_OR_LOSS;
    }

    public void flip() {
        setPlayer(Player.getOpponent(player));
    }

    @Override
    public String toString() {
        String str = "";
        if (gameStatusType == GameStatusType.WIN_OR_LOSS) {
            str += Player.PLAYER_NAMES[Player.getOpponent(player)] + " " + WIN_STR + " ";
        }
        str += GAME_STATUS_STRS[specificGameStatus] + " ";

        return str;
    }

    public boolean isGameOver() {
        return gameStatusType == GameStatusType.WIN_OR_LOSS || gameStatusType == GameStatusType.TIE;
    }

    public int getWinningPlayer() {
        return player;
    }

    public enum GameStatusType {
        TIE, CHECK, GAME_GOES_ON, WIN_OR_LOSS
    }
}

