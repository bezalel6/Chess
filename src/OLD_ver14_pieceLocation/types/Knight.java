package ver14_pieceLocation.types;

import ver14_pieceLocation.Board;
import ver14_pieceLocation.Location;
import ver14_pieceLocation.moves.Move;

import java.util.ArrayList;

public class Knight extends Piece {
    public static int worth = 3;

    public Knight(Location loc, Player pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, types.KNIGHT, "N", hasMoved);
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
