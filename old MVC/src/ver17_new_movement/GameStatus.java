package ver17_new_movement;

public enum GameStatus {
    CHECKMATE("Won By Checkmate"),
    INSUFFICIENT_MATERIAL("Draw by Insufficient Material"),
    OPPONENT_TIMED_OUT("Won On Time"),
    TIME_OUT_VS_INSUFFICIENT_MATERIAL("Draw by Time Out vs Insufficient Material"),
    STALEMATE("Draw by Stalemate"),
    REPETITION("Draw by Repetition"),
    GAME_GOES_ON("");
    private String str;

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
            case OPPONENT_TIMED_OUT:
                return "#";

        }
        return "";
    }

    public String getStr() {
        return str;
    }
}

