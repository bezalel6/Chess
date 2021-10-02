package ver25_pieces_hashmap.model_classes.pieces;

import ver25_pieces_hashmap.Location;
import ver25_pieces_hashmap.model_classes.Board;
import ver25_pieces_hashmap.moves.Move;

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

    public static ArrayList<ArrayList<Move>> createBishopMoves(Location movingFrom, int player, Board board) {
        return createBishopMoves(movingFrom, player, true, board);
    }

    public static ArrayList<ArrayList<Move>> createBishopMoves(Location movingFrom, int player, boolean initializeMoves, Board board) {
        return createBishopMoves(movingFrom, player, BISHOP, initializeMoves, board);
    }

    public static ArrayList<ArrayList<Move>> createBishopMoves(Location movingFrom, int player, int pieceType, boolean initializeMoves, Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();

        for (int j = 0; j < 4; j++) {
            ArrayList<Move> temp = new ArrayList<>();
            for (int i = 1; i < 8; i++) {
                Location tLoc = new Location(myR + combinations[j + 1] * i, myC + combinations[j] * i);
                if (!addMove(movingFrom, tLoc, player, pieceType, temp, initializeMoves, board))
                    break;
            }
            ret.add(temp);
        }
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createBishopMoves(getLoc(), getPieceColor(), board);
    }
}

