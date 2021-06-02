package ver2.types;

import ver2.Location;

import java.util.ArrayList;

public class King extends Piece {
    public King(  Location loc, colors pieceColor) {
        super(3, loc, pieceColor, types.King, "K");
    }

    @Override
    public ArrayList<Path> canMoveTo() {
        ArrayList<Path> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        add(ret,myR+1,myC);
        add(ret,myR,myC+1);
        add(ret,myR+1,myC+1);
        add(ret,myR-1,myC);
        add(ret,myR,myC-1);
        add(ret,myR-1,myC-1);
        add(ret,myR+1,myC-1);
        add(ret,myR-1,myC+1);
        checkLegal(ret);
        return ret;
    }
}
