package ver10_eval_class.types;

import ver10_eval_class.Location;
import ver10_eval_class.Move;

import java.util.ArrayList;

public class Knight extends Piece {
    public static int worth = 3;

    public Knight(Location loc, colors pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, types.KNIGHT, "N", hasMoved);
    }

    @Override
    public ArrayList<Move> canMoveTo(Piece[][] pieces) {
        ArrayList<Move> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();

        add(ret, myR + 1, myC + 2, pieces);
        add(ret, myR + 1, myC - 2, pieces);

        add(ret, myR + 2, myC + 1, pieces);
        add(ret, myR + 2, myC - 1, pieces);

        add(ret, myR - 1, myC + 2, pieces);
        add(ret, myR - 1, myC - 2, pieces);

        add(ret, myR - 2, myC + 1, pieces);
        add(ret, myR - 2, myC - 1, pieces);

        return ret;
    }
}
