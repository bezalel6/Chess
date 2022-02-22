package ver14_correct_piece_location.types;

import ver14_correct_piece_location.Board;
import ver14_correct_piece_location.Location;
import ver14_correct_piece_location.moves.Move;
import ver14_correct_piece_location.moves.PromotionMove;

import java.util.ArrayList;

class EnPassantCaptured {
    private Location captureLoc, pieceLoc;

    public EnPassantCaptured(Location captureLoc, Location pieceLoc) {
        this.captureLoc = captureLoc;
        this.pieceLoc = pieceLoc;
    }

    public EnPassantCaptured(EnPassantCaptured other) {
        captureLoc = new Location(other.captureLoc);
        pieceLoc = new Location(other.pieceLoc);
    }

    public Location getCaptureLoc() {
        return captureLoc;
    }

    public void setCaptureLoc(Location captureLoc) {
        this.captureLoc = captureLoc;
    }

    public Location getPieceLoc() {
        return pieceLoc;
    }

    public void setPieceLoc(Location pieceLoc) {
        this.pieceLoc = pieceLoc;
    }
}

public class Pawn extends Piece {
    public static int worth = 1;
    private int diff = 0;
    private int promotingRow = 0;
    private EnPassantCaptured enPassantCaptured;
    private boolean canGetEnPassant = false;

    public Pawn(Location loc, Player pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, types.PAWN, loc.getColString() + "", hasMoved);
        if (!isWhite()) {
            diff--;
        } else {
            diff++;
            promotingRow = 7;

        }

    }

    public Pawn(Piece other) {
        super(other);
        Pawn pawn = (Pawn) other;
        diff = pawn.diff;
        promotingRow = pawn.promotingRow;
        if (pawn.enPassantCaptured != null)
            enPassantCaptured = new EnPassantCaptured(pawn.enPassantCaptured);
        canGetEnPassant = pawn.canGetEnPassant;
    }

    public String getAnnotation() {
        return getLoc().getColString() + "";
    }


    @Override
    public void setMoved(Move move) {
        if (!getHasMoved() && enPassantCaptured != null && move.getMovingTo().equals(enPassantCaptured.getPieceLoc())) {
            canGetEnPassant = true;
        }
        super.setMoved(move);
    }

    @Override
    public ArrayList<Move> canMoveTo(Board board) {
        ArrayList<Move> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();

        if (isInBounds(myR + diff, myC) && board.getPiece(myR + diff, myC) == null) {
            Location enPassantCapturingLoc = new Location(myR + diff, myC);
            add(ret, enPassantCapturingLoc, board);

            if (isInBounds(myR + (diff * 2), myC) && !getHasMoved() && board.getPiece(myR + (diff * 2), myC) == null) {
                Location loc = new Location(myR + (diff * 2), myC);
//                enPassantCaptured = new EnPassantCaptured(enPassantCapturingLoc, loc);
//                board.setEnPassantTargetLoc(enPassantCapturingLoc);
                Move move = add(ret, loc, board);

            }
        }
        Location leftCapture = new Location(myR + diff, myC - 1), rightCapture = new Location(myR + diff, myC + 1);

        if (isInBounds(rightCapture) && ((board.getPiece(rightCapture) != null && !board.getPiece(rightCapture).isOnMyTeam(this)) || board.getEnPassantTargetSquare() != null && board.getEnPassantTargetSquare().equals(rightCapture))) {
            add(ret, myR + diff, myC + 1, board);
        }
        if (isInBounds(leftCapture) && (board.getPiece(leftCapture) != null && !board.getPiece(leftCapture).isOnMyTeam(this))) {
            add(ret, myR + diff, myC - 1, board);
        }
        checkPromoting(ret, board);
        return ret;
    }

    private void checkPromoting(ArrayList<Move> list, Board board) {
        ArrayList<Move> add = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Move move = list.get(i);
            if (move.getMovingTo().getRow() == promotingRow) {
                list.set(i, new PromotionMove(types.QUEEN, move));
                add.add(new PromotionMove(types.BISHOP, move));
                add.add(new PromotionMove(types.KNIGHT, move));
                add.add(new PromotionMove(types.ROOK, move));
            }
        }
        list.addAll(add);
    }
}
