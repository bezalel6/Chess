package ver34_faster_move_generation.model_classes.pieces;

import ver34_faster_move_generation.Location;
import ver34_faster_move_generation.model_classes.Board;
import ver34_faster_move_generation.model_classes.moves.Move;

import java.util.ArrayList;

public class Bishop extends Piece {
    private static final int[] combinations = new int[]{
            1, 1,
            -1, -1,
            1, -1,
            -1, 1
    };
    public static double worth = 3.2;

    public Bishop(Location loc, int pieceColor) {
        super(loc, pieceColor, BISHOP, "B");
    }

    public Bishop(Piece other) {
        this(other.getStartingLoc(), other.getPieceColor());
        setLoc(other.getLoc());
    }

    public static ArrayList<ArrayList<Move>> createBishopMoves(Location movingFrom, int player) {
        return createBishopMoves(movingFrom, player, BISHOP);
    }

    public static ArrayList<ArrayList<Move>> createBishopMoves(Location movingFrom, int player, int pieceType) {
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();

        for (int j = 0; j < 4; j++) {
            ArrayList<Move> temp = new ArrayList<>();
            for (int i = 1; i < 8; i++) {
                Location tLoc = new Location(myR + combinations[j + 1] * i, myC + combinations[j] * i);
                if (tLoc.isInBounds())
                    temp.add(new Move(movingFrom, tLoc, player, pieceType));
            }
            ret.add(temp);
        }
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves() {
        return createBishopMoves(getLoc(), getPieceColor(), BISHOP);
    }
}

