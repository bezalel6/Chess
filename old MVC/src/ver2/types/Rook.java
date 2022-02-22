package ver2.types;

import ver2.Controller;
import ver2.Location;
import ver2.Model;

import java.util.ArrayList;

public class Rook extends Piece {
    public Rook(  Location loc, colors pieceColor) {
        super(5, loc, pieceColor, types.Rook, "R");
    }

    @Override
    public ArrayList<ver2.types.Path> canMoveTo() {
        ArrayList<ver2.types.Path> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();


        for (int i = 1; i < Controller.COLS; i++) {
            Location loc = new Location(myR, myC + i);
            Piece m = Model.getPiece(loc);
            if (m != null) {
                if (!isOnMyTeam(m))
                    add(ret, loc);
                break;
            }
            if (loc.getCol() > Controller.COLS - 1)
                break;
            add(ret, loc);
        }
        for (int i = 1; i < Controller.COLS; i++) {
            Location loc = new Location(myR, myC - i);
            Piece m = Model.getPiece(loc);
            if (m != null) {
                if (!isOnMyTeam(m))
                    add(ret, loc);
                break;
            }
            if (loc.getCol() < 0)
                break;
            add(ret, loc);
        }


        for (int i = 1; i < Controller.ROWS; i++) {
            Location loc = new Location(myR + i, myC);
            Piece m = Model.getPiece(loc);
            if (m != null) {
                if (!isOnMyTeam(m))
                    add(ret, loc);
                break;
            }
            if (loc.getCol() > Controller.ROWS - 1)
                break;
            add(ret, loc);
        }
        for (int i = 1; i < Controller.ROWS; i++) {
            Location loc = new Location(myR - i, myC);
            Piece m = Model.getPiece(loc);
            if (m != null) {
                if (!isOnMyTeam(m))
                    add(ret, loc);
                break;
            }
            if (loc.getRow() < 0)
                break;
            add(ret, loc);
        }

        return ret;
    }
}
