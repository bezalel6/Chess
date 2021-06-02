package ver9_Dialogs.types;

import ver9_Dialogs.Location;
import ver9_Dialogs.Move;

import java.util.ArrayList;

public class Pawn extends Piece {
    public static int worth = 1;
    public Location enPassant = null;
    private Location enPassantOption = null;
    private Location enPassantLocationToBeAt = null;
    private int diff = 0;
    private int promotingRow = 0;

    public Pawn(Location loc, colors pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, types.PAWN, "", hasMoved);
        if (!isWhite()) {
            diff--;
        } else {
            diff++;
            promotingRow = 7;
        }

    }

    @Override
    public void setMoved() {
        if (enPassantOption != null && getLoc().isEqual(enPassantLocationToBeAt)) {
            enPassant = enPassantOption;
            System.out.println("set en passant on " + enPassantOption.toString());
        } else if (getHasMoved()) {
            enPassant = enPassantOption = null;
        }
        super.setMoved();
    }

    @Override
    public ArrayList<Move> canMoveTo(Piece[][] pieces) {

        ArrayList<Move> ret = new ArrayList();
        Location pieceLoc = getLoc();

        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();

        if (isInBounds(new Location(myR + diff, myC)) && pieces[myR + diff][myC] == null) {
            add(ret, myR + diff, myC, pieces);

            if (isInBounds(new Location(myR + (diff * 2), myC)) && !getHasMoved() && pieces[myR + (diff * 2)][myC] == null) {
                enPassantLocationToBeAt = new Location(myR + (diff * 2), myC);
                add(ret, enPassantLocationToBeAt, pieces);
                enPassantOption = new Location(myR + diff, myC);
            }
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

    private void checkPromoting(ArrayList<Move> list) {
        for (Move move : list) {
            if (move.getMovingTo().getRow() == promotingRow) {
                move.setSpecialMove(Move.SpecialMoves.PROMOTION);
            }
        }
    }
}
