package ver11_board_class.types;

import ver11_board_class.Board;
import ver11_board_class.Location;
import ver11_board_class.moves.EnPassant;
import ver11_board_class.moves.Move;
import ver11_board_class.moves.PromotionMove;
import ver11_board_class.moves.SpecialMoveType;

import java.util.ArrayList;

class EnPassantCaptured {
    private Location captureLoc, pieceLoc;

    public EnPassantCaptured(Location captureLoc, Location pieceLoc) {
        this.captureLoc = captureLoc;
        this.pieceLoc = pieceLoc;
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

    public String getAnnotation() {
        return getLoc().getColString() + "";
    }


    @Override
    public void setMoved(Move move) {
        if (!getHasMoved() && enPassantCaptured != null && move.getMovingFrom().isEqual(enPassantCaptured.getPieceLoc())) {
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
                enPassantCaptured = new EnPassantCaptured(enPassantCapturingLoc, loc);
                add(ret, loc, board);
            }
        }
        Location leftCapture = new Location(myR + diff, myC - 1), rightCapture = new Location(myR + diff, myC + 1);
        Location checkEnPassantLeftCapture = new Location((myR + diff) * 2, myC - 1), checkEnPassantRightCapture = new Location((myR + diff) * 2, myC + 1);

        if (isInBounds(rightCapture) && myC + 1 < COLS && board.getPiece(rightCapture) != null && !board.getPiece(rightCapture).isOnMyTeam(this)) {
            add(ret, myR + diff, myC + 1, board);
        }
        if (isInBounds(leftCapture) && myC - 1 >= 0 && (board.getPiece(leftCapture) != null && !board.getPiece(leftCapture).isOnMyTeam(this))) {
            add(ret, myR + diff, myC - 1, board);
        }
//        add(ret, leftCapture, board);
//        add(ret, rightCapture, board);

        if (isInBounds(checkEnPassantRightCapture)) {
            if (board.getPiece(checkEnPassantRightCapture) != null && board.getPiece(checkEnPassantRightCapture) instanceof Pawn) {
                Pawn pawn = (Pawn) board.getPiece(checkEnPassantRightCapture);
                if (!pawn.isOnMyTeam(this) && pawn.canGetEnPassant && pawn.enPassantCaptured.getCaptureLoc().isEqual(pieceLoc)) {
                    ret.add(new EnPassant(pieceLoc, rightCapture, SpecialMoveType.CAPTURING_EN_PASSANT, checkEnPassantRightCapture, board));
                }
            }
        }
        if (isInBounds(checkEnPassantLeftCapture)) {
            if (board.getPiece(checkEnPassantLeftCapture) != null && board.getPiece(checkEnPassantLeftCapture) instanceof Pawn) {
                Pawn pawn = (Pawn) board.getPiece(checkEnPassantLeftCapture);
                if (!pawn.isOnMyTeam(this) && pawn.canGetEnPassant && pawn.enPassantCaptured.getCaptureLoc().isEqual(pieceLoc)) {
                    ret.add(new EnPassant(pieceLoc, leftCapture, SpecialMoveType.CAPTURING_EN_PASSANT, checkEnPassantLeftCapture, board));
                }
            }
        }
        checkPromoting(ret, board);
        return ret;
    }

    private void checkPromoting(ArrayList<Move> list, Board board) {
        ArrayList<Move> add = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Move move = list.get(i);
            if (move.getMovingFrom().getRow() == promotingRow) {
                list.set(i, new PromotionMove(types.QUEEN, move, board));
                add.add(new PromotionMove(types.BISHOP, move, board));
                add.add(new PromotionMove(types.KNIGHT, move, board));
                add.add(new PromotionMove(types.ROOK, move, board));
            }
        }
        list.addAll(add);
    }
}
