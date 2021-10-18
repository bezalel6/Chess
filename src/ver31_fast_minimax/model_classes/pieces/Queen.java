package ver31_fast_minimax.model_classes.pieces;

import ver31_fast_minimax.Location;
import ver31_fast_minimax.model_classes.Board;
import ver31_fast_minimax.model_classes.moves.Move;

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
        ret.addAll(Rook.createRookMoves(movingFrom, player, QUEEN, initializeMoves, board));
        ret.addAll(Bishop.createBishopMoves(movingFrom, player, QUEEN, initializeMoves, board));
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createQueenMoves(getLoc(), getPieceColor(), board);
    }
}
