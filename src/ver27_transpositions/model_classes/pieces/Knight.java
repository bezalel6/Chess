package ver27_transpositions.model_classes.pieces;

import ver27_transpositions.Location;
import ver27_transpositions.model_classes.Board;
import ver27_transpositions.model_classes.moves.Move;

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

    public Knight(Location loc, int pieceColor) {
        super(loc, pieceColor, KNIGHT, "N");
    }

    public Knight(Piece other) {
        this(other.getStartingLoc(), other.getPieceColor());
        setLoc(other.getLoc());
    }

    public static ArrayList<ArrayList<Move>> createKnightMoves(Location movingFrom, int player, Board board) {
        return createKnightMoves(movingFrom, player, true, board);
    }

    public static ArrayList<ArrayList<Move>> createKnightMoves(Location movingFrom, int player, boolean initializeMoves, Board board) {
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();
        ArrayList<ArrayList<Move>> ret = new ArrayList();

        for (int i = 0; i < combinations.length; i += 2) {
            Location loc = new Location(myR + combinations[i], myC + combinations[i + 1]);
            addSingleMove(new Move(movingFrom, loc, player, KNIGHT, initializeMoves), player, ret, board);
        }
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createKnightMoves(getLoc(), getPieceColor(), board);
    }
}
