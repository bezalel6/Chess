package ver14_pieceLocation;

import java.util.ArrayList;

class Position {
    private String name, FEN;

    public Position(String name, String FEN) {
        this.name = name;
        this.FEN = FEN;
    }

    public String getName() {
        return name;
    }

    public String getFen() {
        return FEN;
    }
}

public class Positions {

    private static Position startingPosition = new Position("Starting Position", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");


    public static ArrayList<Position> getAllPositions() {
        ArrayList ret = new ArrayList();
        ret.add(startingPosition);
        return ret;
    }
}
