package ver2.types;

import ver2.Location;

import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(  Location loc, colors pieceColor) {
        super(3, loc, pieceColor, types.Knight, "N");
    }

    @Override
    public ArrayList<Path> canMoveTo() {
        ArrayList<Path> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();

        add(ret,myR+1,myC+2);
        add(ret,myR+1,myC-2);


        add(ret,myR+2,myC+1);
        add(ret,myR+2,myC-1);


        add(ret,myR-1,myC+2);
        add(ret,myR-1,myC-2);


        add(ret,myR-2,myC+1);
        add(ret,myR-2,myC-1);
        return ret;
    }
}
