package ver18_icon_manager.types;

import ver18_icon_manager.Board;
import ver18_icon_manager.Location;
import ver18_icon_manager.moves.*;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class Pawn extends Piece {
    private int promotingRow = 0;

    public Pawn(Location loc, int pieceColor, boolean hasMoved) {
        super(loc, pieceColor, PAWN, loc.getColString() + "", hasMoved);
        if (!hasMoved)
            if (loc.getRow() != STARTING_ROW[getPieceColor()] + difference) {
                setHasMoved(true);
            }
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
        if (isInBounds(oneStep)) {
            ArrayList<Move> t = new ArrayList<>();
            t.add(new Move(pieceLoc, oneStep, board));
            Location loc = new Location(myR + (difference * 2), myC);
            if (!getHasMoved()) {
                Move newMove = new DoublePawnPush(new Move(getLoc(), loc, false, board), oneStep);
                t.add(newMove);
            }
            ret.add(t);
        }

        Location leftCapture = new Location(myR + difference, myC - 1), rightCapture = new Location(myR + difference, myC + 1);

        if (isInBounds(rightCapture))
            ret.add(new ArrayList<>() {{
                add(new Move(pieceLoc, rightCapture, true, board));
            }});

        if (isInBounds(leftCapture))
            ret.add(new ArrayList<>() {{
                add(new Move(pieceLoc, leftCapture, true, board));
            }});
        checkPromoting(ret);

        return ret;
    }

    public boolean checkEnPassantCapture(Location capturingLoc, Board board) {
        Location enPassantTargetLoc = board.getEnPassantTargetLoc();
        if (enPassantTargetLoc != null && enPassantTargetLoc.equals(capturingLoc)) {
            Location enPassantActualLoc = board.getEnPassantActualLoc();
            Piece piece = board.getPiece(enPassantActualLoc);
            boolean b =
                    enPassantActualLoc != null && piece != null && !piece.isOnMyTeam(getPieceColor());
            return b;
        }
        return false;
    }

    private void checkPromoting(ArrayList<ArrayList<Move>> list) {
        ArrayList<Move> add = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ArrayList<Move> list2 = list.get(i);
            for (int j = 0, list2Size = list2.size(); j < list2Size; j++) {
                Move move = list2.get(j);
                if (move.getMovingTo().getRow() == promotingRow) {
                    list2.set(j, new PromotionMove(QUEEN, move));
                    add.add(new PromotionMove(BISHOP, move));
                    add.add(new PromotionMove(KNIGHT, move));
                    add.add(new PromotionMove(ROOK, move));
                }
            }
            list2.addAll(add);
        }
    }

    @Override
    void checkLegal(ArrayList<Move> addTo, ArrayList<Move> currentlyAdding, Board board) {
        boolean keepAdding = true, add;
        for (Move move : currentlyAdding) {
            add = true;
            Location loc = move.getMovingTo();
            Piece destination = board.getPiece(loc);
            if (destination != null) {
                if (isOnMyTeam(destination) || !move.isCapturing())
                    return;
                keepAdding = false;
            } else if (move.isCapturing()) {
                if (checkEnPassantCapture(move.getMovingTo(), board)) {
                    move = new EnPassant(move);
                } else add = false;
                keepAdding = false;
            }
            if (add) {
                board.applyMove(move);
                if (!board.isInCheck(getPieceColor()))
                    addTo.add(move);
                board.undoMove(move);
            }
            if (!keepAdding)
                break;
        }
    }
}
