package ver8_pruning.types;

import ver8_pruning.Location;
import ver8_pruning.Move;

import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(Location loc, colors pieceColor, boolean hasMoved) {
        super(3, loc, pieceColor, types.KNIGHT, "N",hasMoved);
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
