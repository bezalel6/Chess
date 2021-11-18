package ver36_no_more_location.model_classes.pieces;

import ver36_no_more_location.Location;
import ver36_no_more_location.model_classes.moves.Move;

import java.util.ArrayList;

public class Bishop extends Piece {
    private static final int[] combinations = new int[]{
            1, 1,
            -1, -1,
            1, -1,
            -1, 1
    };
    private static final ArrayList<ArrayList<Move>>[][] preCalculatedMoves = calc();
    public static double worth = 3.2;

    public Bishop(Location loc, int pieceColor) {
        super(loc, pieceColor, BISHOP, "B");
    }

    public Bishop(Piece other) {
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
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();

        for (int j = 0; j < 4; j++) {
            ArrayList<Move> temp = new ArrayList<>();
            for (int i = 1; i < 8; i++) {
                Location tLoc = new Location(myR + combinations[j + 1] * i, myC + combinations[j] * i);
                if (tLoc.isInBounds())
                    temp.add(new Move(movingFrom, tLoc));
            }
            ret.add(temp);
        }
        return ret;
    }

    public static ArrayList<ArrayList<Move>> getPseudoBishopMoves(Location movingFrom) {
        return preCalculatedMoves[movingFrom.getRow()][movingFrom.getCol()];
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves() {
        return getPseudoBishopMoves(getLoc());
    }
}

