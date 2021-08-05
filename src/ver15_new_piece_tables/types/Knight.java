package ver15_new_piece_tables.types;

import ver15_new_piece_tables.Board;
import ver15_new_piece_tables.Location;
import ver15_new_piece_tables.moves.Move;

import java.util.ArrayList;

public class Knight extends Piece {
    public static double worth = 3.1;

    public Knight(Location loc, Player pieceColor, boolean hasMoved) {
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
