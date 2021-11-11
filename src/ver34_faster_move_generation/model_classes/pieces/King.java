package ver34_faster_move_generation.model_classes.pieces;

import ver34_faster_move_generation.Location;
import ver34_faster_move_generation.model_classes.CastlingAbility;
import ver34_faster_move_generation.model_classes.moves.Castling;
import ver34_faster_move_generation.model_classes.moves.Move;

import java.util.ArrayList;
import java.util.Arrays;


public class King extends Piece {
    private static final int[] combinations = new int[]{
            1, 0,
            0, 1,
            1, 1,
            -1, 0,
            0, -1,
            -1, -1,
            1, -1,
            -1, 1
    };
    public static int worth = 200;

    public King(Location loc, int pieceColor) {
        super(loc, pieceColor, KING, "K");
    }

    public King(Piece other) {
        this(other.getStartingLoc(), other.getPieceColor());
        setLoc(other.getLoc());
    }

    public static Location getRookHomeLoc(int player, int side) {
        return new Location(STARTING_ROW[player], 7 * ((side + 1) % 2));
    }

    public static ArrayList<ArrayList<Move>> createKingMoves(Location movingFrom, int player) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();

        for (int i = 0; i < combinations.length; i += 2) {
            Location loc = new Location(myR + combinations[i], myC + combinations[i + 1]);
            if (loc.isInBounds())
                ret.add(new ArrayList<>() {{
                    add(new Move(movingFrom, loc));
                }});
        }
        for (int side = 0; side < 2; side++) {
            int num = side == CastlingAbility.KING_SIDE ? -1 : 1;
            Location rookLoc = getRookHomeLoc(player, side);

            Location kingMiddleLoc = new Location(myR, myC - num);
            Location kingFinalLoc = new Location(myR, myC - (num * 2));
            Location rookFinalLoc = new Location(myR, myC - num);

            ArrayList<Location> params = new ArrayList<>(Arrays.asList(kingMiddleLoc, kingFinalLoc, rookLoc, rookFinalLoc));
            if (side == CastlingAbility.QUEEN_SIDE)
                params.add(new Location(myR, myC - (num * 3)));//rook middle loc
            Castling castling = new Castling(new Move(movingFrom, kingFinalLoc), side, params);
            if (Location.batchCheckBounds(castling.getCastlingLocs()))
                ret.add(new ArrayList<>() {{
                    add(castling);
                }});
        }

        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves() {
        return createKingMoves(getLoc(), getPieceColor());
    }

//    @Override
//    void checkLegal(ArrayList<Move> addTo, ArrayList<Move> currentlyAdding, Board board) {
//        boolean[] castlingAbility = board.getCastlingAbility().getCastlingAbilityArr(getPieceColor());
//        boolean keepAdding = true;
//        for (Move move : currentlyAdding) {
//            Location movingTo = move.getMovingTo();
//            if (!movingTo.isInBounds())
//                return;
//            Piece destination = board.getPiece(movingTo);
//            if (destination != null) {
//                if (move instanceof Castling || isOnMyTeam(destination))
//                    return;
//                move.setCapturing(destination, board);
//                keepAdding = false;
//            } else if (move instanceof Castling) {
//                Castling castlingMove = (Castling) move;
//                if (!castlingAbility[castlingMove.getSide()] || !checkCastlingLocs(castlingMove.getCastlingLocs(), board) || board.isInCheck(getPieceColor())) {
//                    return;
//                }
//            }
//            addIfLegalMove(addTo, move, board);
//            if (!keepAdding)
//                break;
//        }
//
//    }
}
