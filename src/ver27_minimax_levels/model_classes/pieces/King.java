package ver27_minimax_levels.model_classes.pieces;

import ver27_minimax_levels.Location;
import ver27_minimax_levels.model_classes.Board;
import ver27_minimax_levels.model_classes.moves.Castling;
import ver27_minimax_levels.model_classes.moves.Move;

import java.util.ArrayList;
import java.util.Arrays;

import static ver27_minimax_levels.model_classes.moves.Castling.KING_SIDE;
import static ver27_minimax_levels.model_classes.moves.Castling.QUEEN_SIDE;

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

    public static ArrayList<ArrayList<Move>> createKingMoves(Location movingFrom, int player, Board board) {
        return createKingMoves(movingFrom, player, true, board);
    }

    public static ArrayList<ArrayList<Move>> createKingMoves(Location movingFrom, int player, boolean initializeMoves, Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();

        for (int i = 0; i < combinations.length; i += 2) {
            Location loc = new Location(myR + combinations[i], myC + combinations[i + 1]);
            addSingleMove(new Move(movingFrom, loc, player, KING, initializeMoves), player, ret, board);
        }
        if (board.getCastlingAbility().checkAny(player)) {
            for (int side = 0; side < 2; side++) {

                int num = side == KING_SIDE ? -1 : 1;
                Location rookLoc = getRookHomeLoc(player, side);

                Location kingMiddleLoc = new Location(myR, myC - num);
                Location kingFinalLoc = new Location(myR, myC - (num * 2));
                Location rookFinalLoc = new Location(myR, myC - num);

                ArrayList<Location> params = new ArrayList<>(Arrays.asList(kingMiddleLoc, kingFinalLoc, rookLoc, rookFinalLoc));
                if (side == QUEEN_SIDE)
                    params.add(new Location(myR, myC - (num * 3)));//rook middle loc
                Castling castling = new Castling(new Move(movingFrom, kingFinalLoc, player, KING, board), side, params);
                if (Location.batchCheckBounds(castling.getCastlingLocs()))
                    addSingleMove(castling, player, ret, board);
            }
        }
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createKingMoves(getLoc(), getPieceColor(), board);
    }


    boolean checkCastlingLocs(Location[] list, Board board) {
        for (int i = 0; i < list.length; i++) {
            Location loc = list[i];
            if (Castling.ROOK_STARTING_LOC != i) {
                if (!board.isSquareEmpty(loc) || board.isThreatened(loc, getOpponent()))
                    return false;
            } else {
                Piece r = board.getPiece(loc);
                if (!(r instanceof Rook) || !r.isOnMyTeam(this))
                    return false;
            }
        }
        return true;
    }

    @Override
    void checkLegal(ArrayList<Move> addTo, ArrayList<Move> currentlyAdding, Board board) {
        boolean[] castlingAbility = board.getCastlingAbility().getCastlingAbility(getPieceColor());
        boolean keepAdding = true;
        for (Move move : currentlyAdding) {
            Location movingTo = move.getMovingTo();
            Piece destination = board.getPiece(movingTo);
            if (destination != null) {
                if (move instanceof Castling)
                    return;
                move.setCapturing(destination, board);
                keepAdding = false;
            } else if (move instanceof Castling) {
                Castling castlingMove = (Castling) move;
                if (!castlingAbility[castlingMove.getSide()] || !checkCastlingLocs(castlingMove.getCastlingLocs(), board) || board.isInCheck(getPieceColor())) {
                    return;
                }
            }
            addIfLegalMove(addTo, move, board);
            if (!keepAdding)
                break;
        }

    }
}
