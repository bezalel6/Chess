package ver27_transpositions.model_classes.pieces;

import ver27_transpositions.Location;
import ver27_transpositions.Player;
import ver27_transpositions.model_classes.Board;
import ver27_transpositions.model_classes.moves.DoublePawnPush;
import ver27_transpositions.model_classes.moves.EnPassant;
import ver27_transpositions.model_classes.moves.Move;
import ver27_transpositions.model_classes.moves.PromotionMove;

import java.util.ArrayList;


public class Pawn extends Piece {
    public Pawn(Location loc, int pieceColor) {
        super(loc, pieceColor, PAWN, loc.getColString() + "");
        setStartingLoc(new Location(getStartingRow(pieceColor) + getDifference(), loc.getCol()));
    }

    public Pawn(Piece other) {
        this(other.getStartingLoc(), other.getPieceColor());
        setLoc(other.getLoc());
    }

    public static ArrayList<ArrayList<Move>> createPawnMoves(Location movingFrom, int player, Board board) {
        return createPawnMoves(movingFrom, player, false, board, false);
    }

    private static boolean didPawnMove(Location pawnLoc, int player) {
        return pawnLoc.getRow() != STARTING_ROW[player] + getDifference(player);
    }

    private static void checkPromoting(ArrayList<ArrayList<Move>> list, int player) {
        int promotionRow = STARTING_ROW[Player.getOpponent(player)];
        for (ArrayList<Move> list2 : list) {
            ArrayList<Move> addToList = new ArrayList<>();

            for (int j = 0, list2Size = list2.size(); j < list2Size; j++) {
                Move move = list2.get(j);
                if (move.getMovingTo().getRow() == promotionRow) {
                    boolean set = false;

                    for (int promoteTo : CAN_PROMOTE_TO) {
                        PromotionMove promotionMove = new PromotionMove(promoteTo, move);
                        if (!set) {
                            list2.set(j, promotionMove);
                            set = true;
                        } else {
                            addToList.add(promotionMove);
                        }
                    }
                }
            }
            list2.addAll(addToList);
        }
    }

    public static ArrayList<ArrayList<Move>> createPawnMoves(Location movingFrom, int player, boolean initializeMoves, Board board, boolean onlyCaptureMoves) {
        int difference = Piece.getDifference(player);
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        if (!onlyCaptureMoves) {
            Location oneStep = new Location(myR + difference, myC);
            if (addSingleMove(movingFrom, oneStep, player, PAWN, true, ret, board)) {
                Location twoSteps = new Location(myR + (difference * 2), myC);
                if (!didPawnMove(movingFrom, player)) {
                    addSingleMove(new DoublePawnPush(new Move(movingFrom, twoSteps, player, PAWN, initializeMoves, board), oneStep), player, true, ret, board);
                }
            }
        }
        Location capture1 = new Location(myR + difference, myC - 1), capture2 = new Location(myR + difference, myC + 1);

        addSingleMove(new Move(movingFrom, capture2, player, PAWN, Move.TEMP_CAPTURING), player, ret, board);
        addSingleMove(new Move(movingFrom, capture1, player, PAWN, Move.TEMP_CAPTURING), player, ret, board);

        checkPromoting(ret, player);

        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createPawnMoves(getLoc(), getPieceColor(), board);
    }

    public boolean checkEnPassantCapture(Location capturingLoc, Board board) {
        Location enPassantTargetLoc = board.getEnPassantTargetLoc();
        if (enPassantTargetLoc != null && enPassantTargetLoc.equals(capturingLoc)) {
            Location enPassantActualLoc = board.getEnPassantActualLoc();
            Piece piece = board.getPiece(enPassantActualLoc);
            return piece != null && !piece.isOnMyTeam(getPieceColor());
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
                        move.setCapturing(capturingPiece, board);
                        move = new EnPassant(move, capturingPiece.getPieceType());
                    } else
                        return;
                } else
                    move.setCapturing(destination, board);
            }
            addIfLegalMove(addTo, move, board);
        }
    }


    public boolean getHasMoved() {
        return getLoc().getRow() != STARTING_ROW[getPieceColor()] + getDifference() || !getLoc().equals(getStartingLoc());
    }

    public String getAnnotation() {
        return getLoc().getColString() + "";
    }
}
