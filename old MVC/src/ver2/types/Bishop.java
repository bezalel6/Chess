package ver2.types;

import ver2.Controller;
import ver2.Location;
import ver2.Model;

import java.util.ArrayList;

public class Bishop extends Piece {
    public Bishop(Location loc, colors pieceColor) {
        super(3, loc, pieceColor, types.Bishop, "B");
    }

    @Override
    public ArrayList<ver2.types.Path> canMoveTo() {
        ArrayList<ver2.types.Path> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        for (int i = 1; i < Controller.COLS; i++) {
            Location loc = new Location(myR+i, myC + i);
            Piece m = Model.getPiece(loc);
            if (m != null) {
                if (!isOnMyTeam(m))
                    add(ret, loc);
                break;
            }
            if (loc.getCol() > Controller.COLS - 1||loc.getRow() > Controller.COLS - 1)
                break;
            add(ret, loc);
        }
        for (int i = 1; i < Controller.COLS; i++) {
            Location loc = new Location(myR-i, myC - i);
            Piece m = Model.getPiece(loc);
            if (m != null) {
                if (!isOnMyTeam(m))
                    add(ret, loc);
                break;
            }
            if (loc.getCol() < 0||loc.getRow() < 0)
                break;
            add(ret, loc);
        }

        for (int i = 1; i < Controller.COLS; i++) {
            Location loc = new Location(myR+i, myC - i);
            Piece m = Model.getPiece(loc);
            if (m != null) {
                if (!isOnMyTeam(m))
                    add(ret, loc);
                break;
            }
            if (loc.getCol() <0 ||loc.getRow() > Controller.COLS - 1)
                break;
            add(ret, loc);
        }
        for (int i = 1; i < Controller.COLS; i++) {
            Location loc = new Location(myR-i, myC + i);
            Piece m = Model.getPiece(loc);
            if (m != null) {
                if (!isOnMyTeam(m))
                    add(ret, loc);
                break;
            }
            if (loc.getCol() > Controller.COLS - 1||loc.getRow() < 0)
                break;
            add(ret, loc);
        }

        return ret;

    }
}

