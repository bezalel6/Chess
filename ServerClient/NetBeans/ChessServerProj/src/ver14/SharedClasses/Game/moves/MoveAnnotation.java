package ver14.SharedClasses.Game.moves;

import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;

public class MoveAnnotation implements Serializable {
    public static final int PIECE = 0, CAPTURE = 1, DESTINATION = 2, GAME_OVER = 3;

    private static final String CAPTURE_ANN = "x";


    public static String annotate(Move move) {
        return basicAnnotate(move);
    }

    public static String basicAnnotate(BasicMove move) {
        return StrUtils.dontCapWord(move.getMovingFrom() + "" + move.getMovingTo());

    }


}
