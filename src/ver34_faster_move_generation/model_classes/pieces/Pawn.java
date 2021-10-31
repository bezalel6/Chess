package ver34_faster_move_generation.model_classes.pieces;

import ver34_faster_move_generation.Location;
import ver34_faster_move_generation.Player;
import ver34_faster_move_generation.model_classes.Board;
import ver34_faster_move_generation.model_classes.moves.BasicMove;
import ver34_faster_move_generation.model_classes.moves.Move;

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

    public static ArrayList<ArrayList<Move>> createPawnMoves(Location movingFrom, int player) {
        return createPawnMoves(movingFrom, player, false);
    }

    private static boolean didPawnMove(Location pawnLoc, int player) {
        return pawnLoc.getRow() != STARTING_ROW[player] + getDifference(player);
    }

    private static void checkPromoting(ArrayList<ArrayList<Move>> list, int player) {
        int promotionRow = STARTING_ROW[Player.getOpponent(player)];
        for (ArrayList<Move> list2 : list) {
            ArrayList<Move> addToList = new ArrayList<>();

            for (Move move : list2) {
                if (move.getMovingTo().getRow() == promotionRow) {
                    boolean set = false;

                    for (int promoteTo : CAN_PROMOTE_TO) {

                        if (!set) {
                            move.setMoveFlag(Move.MoveFlag.Promotion);
                            move.setPromotingTo(promoteTo);
                            set = true;
                        } else {
                            Move promotionMove = new Move(move);
                            promotionMove.setMoveFlag(Move.MoveFlag.Promotion);
                            promotionMove.setPromotingTo(promoteTo);
                            addToList.add(promotionMove);
                        }
                    }
                }
            }
            list2.addAll(addToList);
        }
    }

    public static ArrayList<ArrayList<Move>> createPawnMoves(Location movingFrom, int player, boolean onlyCaptureMoves) {
        int difference = Piece.getDifference(player);
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        if (!onlyCaptureMoves) {
            Location oneStep = new Location(myR + difference, myC);
            if (oneStep.isInBounds()) {
                Move move = new Move(movingFrom, oneStep, player, PAWN);
                ArrayList<Move> list = new ArrayList<>() {{
                    add(move);
                }};
                Location twoSteps = new Location(myR + (difference * 2), myC);
                if (!didPawnMove(movingFrom, player)) {
                    Move m = new Move(movingFrom, twoSteps, player, PAWN);
                    m.setEnPassantLoc(oneStep);
                    m.setMoveFlag(Move.MoveFlag.DoublePawnPush);
                    list.add(m);
                }
                ret.add(list);
            }
        }
        Location capture1 = new Location(myR + difference, myC - 1), capture2 = new Location(myR + difference, myC + 1);

        if (capture1.isInBounds())
            ret.add(new ArrayList<>() {{
                add(new Move(movingFrom, capture1, player, PAWN, Move.TEMP_CAPTURING));
            }});
        if (capture2.isInBounds())
            ret.add(new ArrayList<>() {{
                add(new Move(movingFrom, capture2, player, PAWN, Move.TEMP_CAPTURING));
            }});
        checkPromoting(ret, player);

        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves() {
        return createPawnMoves(getLoc(), getPieceColor());
    }

//    @Override
//    void checkLegal(ArrayList<Move> addTo, ArrayList<Move> currentlyAdding, Board board) {
//        for (Move move : currentlyAdding) {
//            Location movingTo = move.getMovingTo();
//            if (!movingTo.isInBounds())
//                return;
//            Piece destination = board.getPiece(movingTo);
//            if (destination != null && isOnMyTeam(destination))
//                return;
//            if (move.isCapturing()) {
//                if (destination == null) {
//                    if (checkEnPassantCapture(move.getMovingTo(), board)) {
//                        Piece capturingPiece = board.getPiece(board.getEnPassantActualLoc());
//                        move.setCapturing(capturingPiece, board);
//                        move.setMoveFlag(Move.MoveFlag.EnPassant);
//                        move.setIntermediateMove(new BasicMove(capturingPiece.getLoc(), movingTo));
//                    } else
//                        return;
//                } else
//                    move.setCapturing(destination, board);
//            }
//            addIfLegalMove(addTo, move, board);
//            if (destination != null)
//                return;
//        }
//    }


    public boolean getHasMoved() {
        return getLoc().getRow() != STARTING_ROW[getPieceColor()] + getDifference() || !getLoc().equals(getStartingLoc());
    }

    public String getAnnotation() {
        return getLoc().getColString() + "";
    }
}
