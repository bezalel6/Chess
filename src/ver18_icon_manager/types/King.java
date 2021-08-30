package ver18_icon_manager.types;

import ver18_icon_manager.Board;
import ver18_icon_manager.Location;
import ver18_icon_manager.moves.Castling;
import ver18_icon_manager.moves.CastlingAbility;
import ver18_icon_manager.moves.Move;

import java.util.ArrayList;
import java.util.Arrays;

public class King extends Piece {
    public static int worth = 200;
    private static int[] combinations = new int[]{
            1, 0,
            0, 1,
            1, 1,
            -1, 0,
            0, -1,
            -1, -1,
            1, -1,
            -1, 1
    };

    public King(Location loc, int pieceColor, boolean hasMoved) {
        super(loc, pieceColor, KING, "K", hasMoved);
    }

    public King(Piece other) {
        super(other);
    }

    public static Location getRookHomeLoc(int player, int side) {
        return new Location(STARTING_ROW[player], 7 * ((side + 1) % 2));
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();

        for (int i = 0; i < combinations.length; i += 2) {
            Location loc = new Location(myR + combinations[i], myC + combinations[i + 1]);
            if (isInBounds(loc)) {
                ret.add(new ArrayList<>() {{
                    add(new Move(pieceLoc, loc, false, board));
                }});
            }
        }

        int pieceColor = getPieceColor();
        if (!getHasMoved()) {
            for (int side = 0; side < 2; side++) {

                int num = side == KING_SIDE ? -1 : 1;
                Location rookLoc = getRookHomeLoc(pieceColor, side);

                Location kingMiddleLoc = new Location(myR, myC - num);
                Location kingFinalLoc = new Location(myR, myC - (num * 2));
                Location rookFinalLoc = new Location(myR, myC - num);
                ArrayList<Location> params = new ArrayList<>(Arrays.asList(kingMiddleLoc, kingFinalLoc, rookLoc, rookFinalLoc));
                if (side == QUEEN_SIDE)
                    params.add(new Location(myR, myC - (num * 3)));//rook middle loc
                int finalSide = side;
                ret.add(new ArrayList<>() {{
                    add(new Castling(new Move(pieceLoc, kingFinalLoc, false, board), finalSide, params));
                }});
            }
        }
        return ret;
    }


    boolean batchCheckBoundsAndEmpty(Location[] list, Board board) {
        for (int i = 0; i < list.length; i++) {
            Location loc = list[i];
            if (!isInBounds(loc) || (!board.isSquareEmpty(loc) && Castling.ROOK_STARTING_LOC != i))
                return false;
        }
        return true;
    }

    @Override
    void checkLegal(ArrayList<Move> addTo, ArrayList<Move> currentlyAdding, Board board) {
        boolean[] castlingAbility = board.getCastlingAbility().getCastlingAbility(getPieceColor());
        boolean keepAdding = true;
        for (Move move : currentlyAdding) {
            Piece destination = board.getPiece(move.getMovingTo());
            if (destination != null) {
                if (isOnMyTeam(destination))
                    return;
                move.setCapturing(true);
                keepAdding = false;
            }
            if (move instanceof Castling) {
                Castling castlingMove = (Castling) move;
                //todo check if in check
                if (!batchCheckBoundsAndEmpty(castlingMove.getCastlingLocs(), board) && castlingAbility[castlingMove.getSide()]) {
                    return;
                }
                keepAdding = false;
            }
            board.applyMove(move);
            if (!board.isInCheck(getPieceColor()))
                addTo.add(move);
            board.undoMove(move);

            if (!keepAdding)
                break;
        }
    }
}
