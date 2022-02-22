package ver6.types;

import ver6.Location;

import java.util.ArrayList;

public class Pawn extends Piece {
    private int diff = 0;
    private int promotingRow = 0;

    private Location promotion() {
        return new Location(promotingRow, getLoc().getCol());
    }

    public Location enPassant = null;

    public Pawn(Location loc, colors pieceColor) {
        super(1, loc, pieceColor, types.PAWN, "");
        if (isWhitePerspective()) {
            if (isWhite()) {
                diff--;
            } else {
                diff++;
                promotingRow = 7;
            }
        } else {
            if (!isWhite()) {
                diff--;
            } else {
                diff++;
                promotingRow = 7;
            }
        }
    }

    @Override
    public ArrayList<Path> canMoveTo(Piece[][] pieces) {
        ArrayList<Path> ret = new ArrayList();


        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();

        if (isInBounds(new Location(myR + diff, myC)) && pieces[myR + diff][myC] == null) {
            add(ret, myR + diff, myC, pieces);

            if (isInBounds(new Location(myR + (diff * 2), myC)) && !getHasMoved() && pieces[myR + (diff * 2)][myC] == null) {
                add(ret, myR + (diff * 2), myC, pieces);
                enPassant = new Location(myR + diff, myC);
            } else enPassant = null;
        }
        if (isInBounds(new Location(myR + diff, myC + 1)) && myC + 1 < COLS && (pieces[myR + diff][myC + 1] != null && !pieces[myR + diff][myC + 1].isOnMyTeam(this))) {
            add(ret, myR + diff, myC + 1, pieces);
        }
        if (isInBounds(new Location(myR + diff, myC - 1)) && myC - 1 >= 0 && (pieces[myR + diff][myC - 1] != null && !pieces[myR + diff][myC - 1].isOnMyTeam(this))) {
            add(ret, myR + diff, myC - 1, pieces);
        }
        checkPromoting(ret);
        return ret;
    }

    private void checkPromoting(ArrayList<Path> list) {
        for (Path path : list) {
            Location loc = promotion();
            if (path.getLoc().getRow() == promotingRow) {
                path.setPromoting();
            }
        }
    }
}
