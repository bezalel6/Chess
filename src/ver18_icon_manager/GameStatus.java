package ver18_icon_manager;

public enum GameStatus {
    CHECKMATE("By Checkmate"),
    TIMED_OUT("On Time"),
    INSUFFICIENT_MATERIAL("Draw by Insufficient Material"),
    TIME_OUT_VS_INSUFFICIENT_MATERIAL("Draw by Time Out vs Insufficient Material"),
    STALEMATE("Draw by Stalemate"),
    REPETITION("Draw by Repetition"),
    GAME_GOES_ON("");
    public static final int WINNING_SIDE = 0, LOSING_SIDE = 1, SIDE_NOT_RELEVANT = 2;
    private static final String WIN_STR = "Won", LOSS_STR = "Lost";
    private String str;
    private int side;

    GameStatus(String str) {
        this.str = str;
    }

    public static String getGameStatusAnnotation(GameStatus status) {
        switch (status) {
            case GAME_GOES_ON:
                return "";
            case REPETITION:
            case STALEMATE:
            case INSUFFICIENT_MATERIAL:
            case TIME_OUT_VS_INSUFFICIENT_MATERIAL:
                return "½½";
            case CHECKMATE:
            case TIMED_OUT:
                return "#";

        }
        return "";
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
        if (side != SIDE_NOT_RELEVANT)
            str = "You " + (side == WINNING_SIDE ? WIN_STR : LOSS_STR) + " " + str;
    }

    public int getSideMult() {
        return side == WINNING_SIDE ? 1 : -1;
    }
}

