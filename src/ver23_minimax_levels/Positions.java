package ver23_minimax_levels;

import java.util.ArrayList;
import java.util.Arrays;

public class Positions {

    private static final Position startingPosition = new Position("Starting Position", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");


    public static ArrayList<Position> getAllPositions() {
        return new ArrayList(Arrays.asList(
                startingPosition,
                new Position("Hanging Pieces test", "rnb1kbnr/pppppppp/8/8/8/3q4/PPPPPPPP/RNBQKBNR w KQkq - 0 1")));
    }
}
