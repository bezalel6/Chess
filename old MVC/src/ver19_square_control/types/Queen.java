package ver19_square_control.types;

import ver19_square_control.Board;
import ver19_square_control.Location;
import ver19_square_control.moves.Move;

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
