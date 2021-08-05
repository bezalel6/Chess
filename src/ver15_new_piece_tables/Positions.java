package ver15_new_piece_tables;

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
    private static Position aboutToPromote = new Position("About to Promote", "8/8/4k3/8/8/4K3/p7/8 w - - 0 1");
    private static Position underpromotion = new Position("Underpromotion", "4Q3/Pq4pk/5p1p/5P1K/6PP/8/8/8 w - - 2 2");

    public static ArrayList<Position> getAllPositions() {
        ArrayList ret = new ArrayList();
        ret.add(startingPosition);
        ret.add(aboutToPromote);
        ret.add(underpromotion);
        return ret;
    }
}
