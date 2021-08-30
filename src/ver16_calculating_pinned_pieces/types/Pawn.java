package ver16_calculating_pinned_pieces.types;

import ver16_calculating_pinned_pieces.Board;
import ver16_calculating_pinned_pieces.Location;
import ver16_calculating_pinned_pieces.moves.DoublePawnPush;
import ver16_calculating_pinned_pieces.moves.EnPassant;
import ver16_calculating_pinned_pieces.moves.Move;
import ver16_calculating_pinned_pieces.moves.PromotionMove;

import java.util.ArrayList;


public class Pawn extends Piece {
    public static int worth = 1;
    private int diff = 0;
    private int promotingRow = 0;

    public Pawn(Location loc, int pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, PieceTypes.PAWN, loc.getColString() + "", hasMoved);
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
    }

    public String getAnnotation() {
        return getLoc().getColString() + "";
    }

    @Override
    public ArrayList<Move> canMoveTo(Board board) {
        ArrayList<Move> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();

        Location oneStep = new Location(myR + diff, myC);
        if (board.getPiece(oneStep) == null && add(ret, oneStep, board) != null) {
            if (!getHasMoved()) {
                Location loc = new Location(myR + (diff * 2), myC);
                Move newMove = new DoublePawnPush(new Move(getLoc(), loc, false, board), oneStep);
                add(ret, newMove, board);

            }
        }
        addCaptureMoves(board, ret);
        checkPromoting(ret, board);
        return ret;
    }

    private void addCaptureMoves(Board board, ArrayList list) {
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        Location leftCapture = new Location(myR + diff, myC - 1), rightCapture = new Location(myR + diff, myC + 1);
        if (checkCapture(rightCapture, board)) {
            Move move = new Move(pieceLoc, rightCapture, true, board);
            if (checkEnPassant(rightCapture, board))
                move = new EnPassant(move);
            add(list, move, board);
        }
        if (checkCapture(leftCapture, board)) {
            Move move = new Move(pieceLoc, leftCapture, true, board);
            if (checkEnPassant(leftCapture, board))
                move = new EnPassant(move);
            add(list, move, board);
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
                list.set(i, new PromotionMove(PieceTypes.QUEEN, move));
                add.add(new PromotionMove(PieceTypes.BISHOP, move));
                add.add(new PromotionMove(PieceTypes.KNIGHT, move));
                add.add(new PromotionMove(PieceTypes.ROOK, move));
            }
        }
        list.addAll(add);
    }
}
