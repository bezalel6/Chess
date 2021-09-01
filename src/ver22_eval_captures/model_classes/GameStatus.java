package ver22_eval_captures.model_classes;

public enum GameStatus {
    CHECKMATE("By Checkmate"),
    TIMED_OUT("On Time"),
    INSUFFICIENT_MATERIAL("Draw by Insufficient Material"),
    TIME_OUT_VS_INSUFFICIENT_MATERIAL("Draw by Time Out vs Insufficient Material"),
    STALEMATE("Draw by Stalemate"),
    THREE_FOLD_REPETITION("Draw by Three Fold Repetition"),
    FIFTY_MOVE_RULE("Draw by Fifty Move Rule"),
    GAME_GOES_ON("");
    public static final int WINNING_SIDE = 0, LOSING_SIDE = 1, SIDE_NOT_RELEVANT = 2;
    public static final String DRAW_ANN = "½½", MATE_ANN = "#";
    private static final String WIN_STR = "Won", LOSS_STR = "Lost";
    private String str;
    private int side;
    private boolean didSetStr = false;

    GameStatus(String str) {
        this.str = str;
    }

    public static String getGameStatusAnnotation(GameStatus status) {
        switch (status) {
            case GAME_GOES_ON:
                return "";
            case THREE_FOLD_REPETITION:
            case STALEMATE:
            case INSUFFICIENT_MATERIAL:
            case TIME_OUT_VS_INSUFFICIENT_MATERIAL:
            case FIFTY_MOVE_RULE:
                return DRAW_ANN;
            case CHECKMATE:
            case TIMED_OUT:
                return MATE_ANN;

        }
        return "";
    }

    //    public static
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
}

