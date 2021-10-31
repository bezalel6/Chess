package ver34_faster_move_generation.model_classes.pieces;

import ver34_faster_move_generation.Location;
import ver34_faster_move_generation.model_classes.moves.Move;

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

    public static ArrayList<ArrayList<Move>> createQueenMoves(Location movingFrom) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        ret.addAll(Rook.getPseudoRookMoves(movingFrom));
        ret.addAll(Bishop.getPseudoBishopMoves(movingFrom));
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves() {
        return createQueenMoves(getLoc());
    }
}
