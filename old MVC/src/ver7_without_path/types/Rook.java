package ver7_without_path.types;

import ver7_without_path.Location;
import ver7_without_path.Move;

import java.util.ArrayList;

public class Rook extends Piece {
    public Rook(Location loc, colors pieceColor) {
        super(5, loc, pieceColor, types.ROOK, "R");
    }

    @Override
    public ArrayList<Move> canMoveTo(Piece[][] pieces) {
        ArrayList<Move> ret = new ArrayList();
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
