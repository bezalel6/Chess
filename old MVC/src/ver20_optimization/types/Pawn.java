package ver20_optimization.types;

import ver20_optimization.model_classes.Board;
import ver20_optimization.Location;
import ver20_optimization.moves.DoublePawnPush;
import ver20_optimization.moves.EnPassant;
import ver20_optimization.moves.Move;
import ver20_optimization.moves.PromotionMove;

import java.util.ArrayList;


public class Pawn extends Piece {
    public Pawn(Location loc, int pieceColor) {
        super(loc, pieceColor, PAWN, loc.getColString() + "");
    }

    public Pawn(Piece other) {
        super(other);
        super.difference = other.difference;
    }

    public static ArrayList<ArrayList<Move>> createPawnMoves(Piece piece, Board board) {
        Location pieceLoc = piece.getLoc();
        int difference = piece.difference;
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        Location oneStep = new Location(myR + difference, myC);
        if (addSingleMove(oneStep, piece, true, ret, board)) {
            Location twoSteps = new Location(myR + (difference * 2), myC);
            if (!piece.getHasMoved()) {
                addSingleMove(new DoublePawnPush(new Move(pieceLoc, twoSteps, board), oneStep), piece, true, ret, board);
            }
        }

        Location capture1 = new Location(myR + difference, myC - 1), capture2 = new Location(myR + difference, myC + 1);

        addSingleMove(new Move(pieceLoc, capture2, Move.TEMP_CAPTURING_HASH, board), piece, ret, board);
        addSingleMove(new Move(pieceLoc, capture1, Move.TEMP_CAPTURING_HASH, board), piece, ret, board);

        checkPromoting(ret, piece);

        return ret;
    }

    private static void checkPromoting(ArrayList<ArrayList<Move>> list, Piece piece) {
        int promotionRow = STARTING_ROW[piece.getOpponent()];
        for (int i = 0; i < list.size(); i++) {
            ArrayList<Move> list2 = list.get(i);

            ArrayList<Move> add = new ArrayList<>();

            for (int j = 0, list2Size = list2.size(); j < list2Size; j++) {
                Move move = list2.get(j);
                if (move.getMovingTo().getRow() == promotionRow) {
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
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createPawnMoves(this, board);
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

    @Override
    void checkLegal(ArrayList<Move> addTo, ArrayList<Move> currentlyAdding, Board board) {
        for (Move move : currentlyAdding) {
            Location movingTo = move.getMovingTo();
            Piece destination = board.getPiece(movingTo);
            if (move.isCapturing()) {
                if (destination == null) {
                    if (checkEnPassantCapture(move.getMovingTo(), board)) {
                        Piece capturingPiece = board.getPiece(board.getEnPassantActualLoc());
                        move = new EnPassant(move, capturingPiece.hashCode());
                    } else
                        return;
                } else
                    move.setCapturing(destination.hashCode());
            }
            addIfLegalMove(addTo, move, board);
        }
    }


    public boolean getHasMoved() {
        return getLoc().getRow() != STARTING_ROW[getPieceColor()] + difference || !getLoc().equals(getStartingLoc());
    }

    public String getAnnotation() {
        return getLoc().getColString() + "";
    }
}
