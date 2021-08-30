package ver16_calculating_pinned_pieces.types;

import ver16_calculating_pinned_pieces.Board;
import ver16_calculating_pinned_pieces.Location;
import ver16_calculating_pinned_pieces.moves.Move;

import java.util.ArrayList;

public class Bishop extends Piece {
    public static double worth = 3.2;

    public Bishop(Location loc, int pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, PieceTypes.BISHOP, "B", hasMoved);
    }

    public Bishop(Piece other) {
        super(other);
    }

    @Override
    public ArrayList<Move> canMoveTo(Board board) {
        ArrayList<Move> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR + i, myC + i);
            if (add(ret, loc, board) == null) break;
        }
        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR - i, myC - i);
            if (add(ret, loc, board) == null) break;
        }

        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR + i, myC - i);
            if (add(ret, loc, board) == null) break;
        }
        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR - i, myC + i);
            if (add(ret, loc, board) == null) break;
        }
        return ret;

    }
}

