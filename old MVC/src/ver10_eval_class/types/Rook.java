package ver10_eval_class.types;

import ver10_eval_class.Location;
import ver10_eval_class.Move;

import java.util.ArrayList;

public class Rook extends Piece {
    public static int worth = 5;

    public Rook(Location loc, colors pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, types.ROOK, "R", hasMoved);
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
