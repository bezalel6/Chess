package ver19_square_control;

import java.util.ArrayList;
import java.util.Arrays;

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
    private static Position aboutToPromote = new Position("About to Promote", "8/8/4k3/8/8/4K3/p7/1P6 b - - 0 1");
    private static Position castling = new Position("Castling", "r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w KQkq - 0 1");
    private static Position underpromotion = new Position("Underpromotion", "4Q3/Pq4pk/5p1p/5P1K/6PP/8/8/8 w - - 2 2");
    private static Position rook = new Position("Rook", "K7/8/8/4R3/8/8/8/k7 w - - 2 2");
    private static Position bishop = new Position("Bishop", "1k6/8/8/4q3/5B2/8/7K/8 b - - 0 1");
    private static Position knight = new Position("Knight", "1k6/8/8/4q3/5N2/8/7K/8 b - - 0 1");
    private static Position king = new Position("King", "Q7/8/8/4k3/8/8/8/K7 w - - 2 2");
    private static Position rooks = new Position("2 Rooks", "8/2k5/8/2q5/8/2R5/2R4K/8 w - - 0 1");
    private static Position pins = new Position("Pins", "r3k3/1p3p2/p2q2p1/bn3P2/1N2PQP1/PB6/3K1R1r/3R4 w - - 0 1");
    private static Position testPosition = new Position("Test Position", "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");


    public static ArrayList<Position> getAllPositions() {
        ArrayList ret = new ArrayList(Arrays.asList(startingPosition, aboutToPromote, castling, underpromotion, rook, bishop, king, rooks, knight, pins, testPosition));

        return ret;
    }
}
