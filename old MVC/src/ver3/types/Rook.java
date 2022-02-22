package ver3.types;

import ver3.Location;

import java.util.ArrayList;

public class Rook extends Piece {
    public Rook(Location loc, int pieceColor) {
        super(5, loc, pieceColor, ROOK, "R");
    }

    @Override
    public ArrayList<ver3.types.Path> canMoveTo(Piece[][] pieces, boolean isCheckKing) {
        ArrayList<ver3.types.Path> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();

        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR, myC + i);
            if (!isInBounds(loc)) break;
            Piece piece = pieces[loc.getRow()][loc.getCol()];
            if (piece != null) {
                if (!isOnMyTeam(piece))
                    add(ret, loc, pieces);
                break;
            }
            if (loc.getCol() > COLS - 1)
                break;
            add(ret, loc, pieces);
        }
        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR, myC - i);
            if (!isInBounds(loc)) break;
            Piece piece = pieces[loc.getRow()][loc.getCol()];
            if (piece != null) {
                if (!isOnMyTeam(piece))
                    add(ret, loc, pieces);
                break;
            }
            if (loc.getCol() < 0)
                break;
            add(ret, loc, pieces);
        }

        for (int i = 1; i < ROWS; i++) {
            Location loc = new Location(myR + i, myC);
            if (!isInBounds(loc)) break;
            Piece piece = pieces[loc.getRow()][loc.getCol()];
            if (piece != null) {
                if (!isOnMyTeam(piece))
                    add(ret, loc, pieces);
                break;
            }
            if (loc.getCol() > ROWS - 1)
                break;
            add(ret, loc, pieces);
        }
        for (int i = 1; i < ROWS; i++) {
            Location loc = new Location(myR - i, myC);
            if (!isInBounds(loc)) break;
            Piece piece = pieces[loc.getRow()][loc.getCol()];
            if (piece != null) {
                if (!isOnMyTeam(piece))
                    add(ret, loc, pieces);
                break;
            }
            if (loc.getRow() < 0)
                break;
            add(ret, loc, pieces);
        }
        return ret;
    }
}
