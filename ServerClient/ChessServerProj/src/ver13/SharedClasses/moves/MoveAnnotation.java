package ver13.SharedClasses.moves;

import java.io.Serializable;

public class MoveAnnotation implements Serializable {
    public static final int PIECE = 0, CAPTURE = 1, DESTINATION = 2, GAME_OVER = 3;

    private static final String CAPTURE_ANN = "x";


    public static String annotate(Move move) {
        return move.getBasicMoveAnnotation();
    }
}
