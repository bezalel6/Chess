package ver21_square_class.types;

import ver21_square_class.Location;
import ver21_square_class.model_classes.Board;
import ver21_square_class.moves.Move;

import java.util.ArrayList;

public class Queen extends Piece {
    public static int worth = 9;

    public Queen(Location loc, int pieceColor) {
        super(loc, pieceColor, QUEEN, "Q");
    }

    public Queen(Piece other) {
        super(other);
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        ret.addAll(Rook.createRookMoves(this, board));
        ret.addAll(Bishop.createBishopMoves(this, board));
        return ret;
    }
}
