package ver7_without_path.types;

import ver7_without_path.Location;
import ver7_without_path.Move;

import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(Location loc, colors pieceColor) {
        super(3, loc, pieceColor, types.KNIGHT, "N");
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
