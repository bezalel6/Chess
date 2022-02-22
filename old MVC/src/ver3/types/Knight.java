package ver3.types;

import ver3.Location;

import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(Location loc, int pieceColor) {
        super(3, loc, pieceColor, KNIGHT, "N");
    }

    @Override
    public ArrayList<ver3.types.Path> canMoveTo(Piece[][] pieces, boolean isCheckKing) {
        ArrayList<Path> ret = new ArrayList();
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
