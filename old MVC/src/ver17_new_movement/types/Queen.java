package ver17_new_movement.types;

import ver17_new_movement.Board;
import ver17_new_movement.Location;
import ver17_new_movement.moves.Move;

import java.util.ArrayList;

public class Queen extends Piece {
    public static int worth = 9;

    public Queen(Location loc, int pieceColor, boolean hasMoved) {
        super(loc, pieceColor, QUEEN, "Q", hasMoved);
    }

    public Queen(Piece other) {
        super(other);
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        Location loc = getLoc();
        int pieceColor = getPieceColor();
        ret.addAll(new Rook(loc, pieceColor, true).generatePseudoMoves(board));
        ret.addAll(new Bishop(loc, pieceColor, true).generatePseudoMoves(board));
        return ret;
    }
}