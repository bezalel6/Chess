package ver22_eval_captures.types;

import ver22_eval_captures.Location;
import ver22_eval_captures.model_classes.Board;
import ver22_eval_captures.moves.Move;

import java.util.ArrayList;

public class Queen extends Piece {
    public static int worth = 9;

    public Queen(Location loc, int pieceColor) {
        super(loc, pieceColor, QUEEN, "Q");
    }

    public Queen(Piece other) {
        super(other);
    }

    public static ArrayList<ArrayList<Move>> createQueenMoves(Location movingFrom, int player, Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        ret.addAll(Rook.createRookMoves(movingFrom, player, board));
        ret.addAll(Bishop.createBishopMoves(movingFrom, player, board));
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createQueenMoves(getLoc(), getPieceColor(), board);
    }
}
