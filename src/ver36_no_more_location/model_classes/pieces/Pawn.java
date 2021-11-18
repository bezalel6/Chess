package ver36_no_more_location.model_classes.pieces;

import ver36_no_more_location.Location;
import ver36_no_more_location.Player;
import ver36_no_more_location.model_classes.moves.Move;

import java.util.ArrayList;


public class Pawn extends Piece {
    private static final ArrayList<ArrayList<Move>>[][][] preCalculatedMoves = calc();

    public Pawn(Location loc, int pieceColor) {
        super(loc, pieceColor, PAWN, loc.getColString() + "");
        setStartingLoc(new Location(getStartingRow(pieceColor) + getDifference(), loc.getCol()));
    }

    public Pawn(Piece other) {
        this(other.getStartingLoc(), other.getPieceColor());
        setLoc(other.getLoc());
    }

    private static boolean didPawnMove(Location pawnLoc, int player) {
        return pawnLoc.getRow() != STARTING_ROW[player] + getDifference(player);
    }

    private static ArrayList<ArrayList<Move>>[][][] calc() {
        ArrayList<ArrayList<Move>>[][][] ret = new ArrayList[2][8][8];
        for (int player : Player.PLAYERS) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Location loc = new Location(row, col);
                    ret[player][row][col] = generateMoves(loc, player);
                }
            }
        }
        return ret;
    }

    private static ArrayList<ArrayList<Move>> generateMoves(Location movingFrom, int player) {
        int difference = Piece.getDifference(player);
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        Location oneStep = new Location(myR + difference, myC);
        if (oneStep.isInBounds()) {
            Move move = new Move(movingFrom, oneStep);
            ArrayList<Move> list = new ArrayList<>() {{
                add(move);
            }};
            Location twoSteps = new Location(myR + (difference * 2), myC);
            if (!didPawnMove(movingFrom, player)) {
                Move m = new Move(movingFrom, twoSteps);
                m.setEnPassantLoc(oneStep);
                m.setMoveFlag(Move.MoveFlag.DoublePawnPush);
                list.add(m);
            }
            ret.add(list);
        }
        ret.addAll(createCaptureMoves(movingFrom, player));
        checkPromoting(ret, player);

        return ret;
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

    public static ArrayList<ArrayList<Move>> getPseudoPawnMoves(Location movingFrom, int player) {
        return preCalculatedMoves[player][movingFrom.getRow()][movingFrom.getCol()];
    }

    public static ArrayList<ArrayList<Move>> createCaptureMoves(Location movingFrom, int player) {
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        int difference = Piece.getDifference(player);
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();
        Location capture1 = new Location(myR + difference, myC - 1), capture2 = new Location(myR + difference, myC + 1);

        if (capture1.isInBounds())
            ret.add(new ArrayList<>() {{
                add(new Move(movingFrom, capture1, Move.TEMP_CAPTURING));
            }});
        if (capture2.isInBounds())
            ret.add(new ArrayList<>() {{
                add(new Move(movingFrom, capture2, Move.TEMP_CAPTURING));
            }});
        return ret;

    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves() {
        return getPseudoPawnMoves(getLoc(), getPieceColor());
    }


    public boolean getHasMoved() {
        return getLoc().getRow() != STARTING_ROW[getPieceColor()] + getDifference() || !getLoc().equals(getStartingLoc());
    }

    public String getAnnotation() {
        return getLoc().getColString() + "";
    }
}
