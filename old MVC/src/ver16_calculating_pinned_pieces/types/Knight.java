package ver16_calculating_pinned_pieces.types;

import ver16_calculating_pinned_pieces.Board;
import ver16_calculating_pinned_pieces.Location;
import ver16_calculating_pinned_pieces.moves.Move;

import java.util.ArrayList;

public class Knight extends Piece {
    public static double worth = 3.1;

    public Knight(Location loc, int pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, PieceTypes.KNIGHT, "N", hasMoved);
    }

    public Knight(Piece other) {
        super(other);
    }

    @Override
    public ArrayList<Move> canMoveTo(Board board) {
        ArrayList<Move> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();

        add(ret, myR + 1, myC + 2, board);
        add(ret, myR + 1, myC - 2, board);

        add(ret, myR + 2, myC + 1, board);
        add(ret, myR + 2, myC - 1, board);

        add(ret, myR - 1, myC + 2, board);
        add(ret, myR - 1, myC - 2, board);

        add(ret, myR - 2, myC + 1, board);
        add(ret, myR - 2, myC - 1, board);

        return ret;
    }
}
