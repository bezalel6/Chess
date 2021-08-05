package ver14_pieceLocation;

public enum GameStatus {
    CHECKMATE, INSUFFICIENT_MATERIAL, OPPONENT_TIMED_OUT, TIME_OUT_VS_INSUFFICIENT_MATERIAL, STALEMATE, REPETITION, GAME_GOES_ON, LOSS, FIFTY_MOVES;

    public static String getGameStatusAnnotation(GameStatus status) {
        switch (status) {
            case GAME_GOES_ON:
                return "";
            case REPETITION:
            case STALEMATE:
            case INSUFFICIENT_MATERIAL:
            case TIME_OUT_VS_INSUFFICIENT_MATERIAL:
            case FIFTY_MOVES:
                return "½½";
            case CHECKMATE:
            case OPPONENT_TIMED_OUT:
                return "#";

        }
        return "";
    }
}
