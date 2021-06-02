package ver3.types;

import ver3.Location;

import java.util.ArrayList;

public class King extends Piece {
    public Castling kingSide, queenSide;

    public King(Location loc, int pieceColor) {
        super(3, loc, pieceColor, KING, "K");
    }

    @Override
    public ArrayList<Path> canMoveTo(Piece[][] pieces, boolean isCheckKing) {
        kingSide = null;
        queenSide = null;
        ArrayList<Path> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        add(ret, myR + 1, myC, pieces);
        add(ret, myR, myC + 1, pieces);
        add(ret, myR + 1, myC + 1, pieces);
        add(ret, myR - 1, myC, pieces);
        add(ret, myR, myC - 1, pieces);
        add(ret, myR - 1, myC - 1, pieces);
        add(ret, myR + 1, myC - 1, pieces);
        add(ret, myR - 1, myC + 1, pieces);
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (!getHasMoved() && p != null && p.isOnMyTeam(this) && p instanceof Rook && !p.getHasMoved()) {
                    Location rookLoc = p.getLoc();

                    if (myC > rookLoc.getCol()) {
                        queenSide = new Castling((Rook) p, new Location(myR, myC - 3), new Location(myR, myC - 2), new Location(myR, myC - 1));
                    } else {
                        kingSide = new Castling((Rook) p, new Location(myR, myC + 1), new Location(myR, myC + 2), new Location(myR, myC + 1));
                    }

                }
            }
        }
        return ret;
    }

}
