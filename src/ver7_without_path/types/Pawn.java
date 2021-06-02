package ver7_without_path.types;

import ver7_without_path.Location;
import ver7_without_path.Move;

import java.util.ArrayList;

public class Pawn extends Piece {
    public Location enPassant = null;
    private int diff = 0;
    private int promotingRow = 0;

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

    private Location promotion() {
        return new Location(promotingRow, getLoc().getCol());
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
                Move thisMove = add(ret, myR + (diff * 2), myC, pieces);
                thisMove.setSpecialMove(Move.SpecialMoves.EN_PASSANT);
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

    private void checkPromoting(ArrayList<Move> list) {
        for (Move move : list) {
            if (move.getMovingTo().getRow() == promotingRow) {
                move.setSpecialMove(Move.SpecialMoves.PROMOTION);
            }
        }
    }
}
