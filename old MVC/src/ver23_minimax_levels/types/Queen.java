package ver23_minimax_levels.types;

import ver23_minimax_levels.Location;
import ver23_minimax_levels.model_classes.Board;
import ver23_minimax_levels.moves.Move;

import java.util.ArrayList;

public class Queen extends Piece {
    public static int worth = 9;

    public Queen(Location loc, int pieceColor) {
        super(loc, pieceColor, QUEEN, "Q");
    }

    public Queen(Piece other) {
        this(other.getStartingLoc(), other.getPieceColor());
        setLoc(other.getLoc());
    }

    public static ArrayList<ArrayList<Move>> createQueenMoves(Location movingFrom, int player, Board board) {
        return createQueenMoves(movingFrom, player, true, board);
    }

    public static ArrayList<ArrayList<Move>> createQueenMoves(Location movingFrom, int player, boolean initializeMoves, Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        ret.addAll(Rook.createRookMoves(movingFrom, player, initializeMoves, board));
        ret.addAll(Bishop.createBishopMoves(movingFrom, player, initializeMoves, board));
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createQueenMoves(getLoc(), getPieceColor(), board);
    }
}
