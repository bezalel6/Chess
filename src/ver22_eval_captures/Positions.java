package ver22_eval_captures;

import java.util.ArrayList;
import java.util.Arrays;

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
    private static Position testPosition = new Position("Mate with 2 rooks", "5k2/8/8/1R6/1R6/4K3/8/8 w - - 0 1");
    private static Position testBug = new Position("BUGG", "r1bqkb1r/ppp1pppp/2n5/3n4/3P4/5N2/PPP1PPPP/R1BQKB1R w KQkq - 0 5");
    private static Position endgamePos = new Position("Endgame Position", "8/K7/8/8/8/Q7/1Q6/5k2 w - - 0 1");
    private static Position mateIn4 = new Position("M4", "7R/r1p1q1pp/3k4/1p1n1Q2/3N4/8/1PP2PPP/2B3K1 w - - 1 0");


    public static ArrayList<Position> getAllPositions() {
        ArrayList ret = new ArrayList(Arrays.asList(startingPosition, aboutToPromote, castling, underpromotion, rook, bishop, king, rooks, knight, pins, testPosition, testBug, endgamePos, mateIn4));

        return ret;
    }
}
