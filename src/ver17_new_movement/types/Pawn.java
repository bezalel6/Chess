package ver17_new_movement.types;

import ver17_new_movement.Board;
import ver17_new_movement.Location;
import ver17_new_movement.moves.DoublePawnPush;
import ver17_new_movement.moves.EnPassant;
import ver17_new_movement.moves.Move;
import ver17_new_movement.moves.PromotionMove;

import java.util.ArrayList;
import java.util.Arrays;


public class Pawn extends Piece {
    private int promotingRow = 0;

    public Pawn(Location loc, int pieceColor, boolean hasMoved) {
        super(loc, pieceColor, PAWN, loc.getColString() + "", hasMoved);
        if (isWhite()) {
            promotingRow = 7;
        }

    }

    public Pawn(Piece other) {
        super(other);
        Pawn pawn = (Pawn) other;
        super.difference = other.difference;
        promotingRow = pawn.promotingRow;
    }

    public String getAnnotation() {
        return getLoc().getColString() + "";
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        Location oneStep = new Location(myR + difference, myC);
        ArrayList<Move> t = new ArrayList<>();
        t.add(new Move(pieceLoc, oneStep, board));
        if (board.getPiece(oneStep) == null) {
            if (!getHasMoved()) {
                Location loc = new Location(myR + (difference * 2), myC);
                Move newMove = new DoublePawnPush(new Move(getLoc(), loc, false, board), oneStep);
                t.add(newMove);

            }
        }
        checkPromoting(t, board);
        ret.add(t);
        t = new ArrayList<>();
        addCaptureMoves(board, t);
        ret.add(t);
        return ret;
    }

    private void addCaptureMoves(Board board, ArrayList list) {
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        Location leftCapture = new Location(myR + difference, myC - 1), rightCapture = new Location(myR + difference, myC + 1);
        if (checkCapture(rightCapture, board)) {
            Move move = new Move(pieceLoc, rightCapture, true, board);
            if (checkEnPassant(rightCapture, board))
                move = new EnPassant(move);
            list.add(move);
        }
        if (checkCapture(leftCapture, board)) {
            Move move = new Move(pieceLoc, leftCapture, true, board);
            if (checkEnPassant(leftCapture, board))
                move = new EnPassant(move);
            list.add(move);
        }
    }

    private boolean checkCapture(Location loc, Board board) {
        return isInBounds(loc) && ((board.getPiece(loc) != null && !board.getPiece(loc).isOnMyTeam(this)) || checkEnPassant(loc, board));
    }

    private boolean checkEnPassant(Location capturingLoc, Board board) {
        Location epsn = board.getEnPassantTargetSquare();
        return epsn != null && epsn.equals(capturingLoc);
    }

    private void checkPromoting(ArrayList<Move> list, Board board) {
        ArrayList<Move> add = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Move move = list.get(i);
            if (move.getMovingTo().getRow() == promotingRow) {
                list.set(i, new PromotionMove(QUEEN, move));
                add.add(new PromotionMove(BISHOP, move));
                add.add(new PromotionMove(KNIGHT, move));
                add.add(new PromotionMove(ROOK, move));
            }
        }
        list.addAll(add);
    }
}
