package ver24_flip_board.model_classes;

import ver24_flip_board.moves.MoveAnnotation;

import static ver24_flip_board.moves.MoveAnnotation.*;

public enum GameStatus {
    CHECKMATE("By Checkmate"),
    TIMED_OUT("On Time"),
    INSUFFICIENT_MATERIAL("Draw by Insufficient Material", TIE),
    TIME_OUT_VS_INSUFFICIENT_MATERIAL("Draw by Time Out vs Insufficient Material", TIE),
    STALEMATE("Draw by Stalemate", TIE),
    THREE_FOLD_REPETITION("Draw by Three Fold Repetition", TIE),
    FIFTY_MOVE_RULE("Draw by Fifty Move Rule", TIE),
    GAME_GOES_ON("", MoveAnnotation.GAME_GOES_ON),
    CHECK(CHECK_ANN, MoveAnnotation.CHECK);
    public static final int WINNING_SIDE = 0, LOSING_SIDE = 1, SIDE_NOT_RELEVANT = 2;

    private static final String WIN_STR = "Won", LOSS_STR = "Lost";
    private int gameStatusType;
    private String str;
    private int side;
    private boolean didSetStr = false;

    GameStatus(String str) {
        this(str, WIN_OR_LOSS);
    }

    GameStatus(String str, int gameStatusType) {
        this.str = str;
        this.gameStatusType = gameStatusType;
    }

    public static String getGameStatusAnnotation(GameStatus status) {
        return GAME_STATUS_ANNOTATIONS[status.gameStatusType];
    }

    public int getGameStatusType() {
        return gameStatusType;
    }

    public void setGameStatusType(int gameStatusType) {
        this.gameStatusType = gameStatusType;
    }

    public String getStr() {
        return str;
    }

    public int getSide() {
        return side;
    }

    /**
     * is this game status for the winning or losing side?
     * concatenates the str with the fitting str
     *
     * @param side
     */
    public void setSide(int side) {
        this.side = side;
        if (side != SIDE_NOT_RELEVANT && !didSetStr) {
            didSetStr = true;
            str = "You " + (side == WINNING_SIDE ? WIN_STR : LOSS_STR) + " " + str;
        }
    }

    public int getSideMult() {
        return side == WINNING_SIDE ? 1 : -1;
    }

    public boolean isCheck() {
//        todo || is checkmate (always check. it might be mate!
        return gameStatusType == MoveAnnotation.CHECK;
    }
}

