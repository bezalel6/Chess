package ver36_no_more_location.model_classes.pieces;

import ver36_no_more_location.Location;
import ver36_no_more_location.model_classes.moves.Move;

import java.util.ArrayList;

public class Knight extends Piece {
    private static final int[] combinations = new int[]{
            1, 2,
            1, -2,
            2, 1,
            2, -1,
            -1, 2,
            -1, -2,
            -2, 1,
            -2, -1
    };
    private static final ArrayList<ArrayList<Move>>[][] preCalculatedMoves = calc();

    public Knight(Location loc, int pieceColor) {
        super(loc, pieceColor, KNIGHT, "N");
    }

    public Knight(Piece other) {
        this(other.getStartingLoc(), other.getPieceColor());
        setLoc(other.getLoc());
    }

    private static ArrayList<ArrayList<Move>>[][] calc() {
        ArrayList<ArrayList<Move>>[][] ret = new ArrayList[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Location loc = new Location(row, col);
                ret[row][col] = generateMoves(loc);
            }
        }
        return ret;
    }

    private static ArrayList<ArrayList<Move>> generateMoves(Location movingFrom) {
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();
        ArrayList<ArrayList<Move>> ret = new ArrayList();

        for (int i = 0; i < combinations.length; i += 2) {
            Location loc = new Location(myR + combinations[i], myC + combinations[i + 1]);
            if (loc.isInBounds())
                ret.add(new ArrayList<>() {{
                    add(new Move(movingFrom, loc));
                }});
        }
        return ret;
    }

    public static ArrayList<ArrayList<Move>> getPseudoKnightMoves(Location movingFrom) {
        return preCalculatedMoves[movingFrom.getRow()][movingFrom.getCol()];
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves() {
        return getPseudoKnightMoves(getLoc());
    }
}
