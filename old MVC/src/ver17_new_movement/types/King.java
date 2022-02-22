package ver17_new_movement.types;

import ver17_new_movement.Board;
import ver17_new_movement.Location;
import ver17_new_movement.moves.Castling;
import ver17_new_movement.moves.CastlingAbility;
import ver17_new_movement.moves.Move;
import ver17_new_movement.moves.SpecialMoveType;

import java.lang.reflect.Array;
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

        ArrayList<Move> temp = new ArrayList<>();
        for (int i = 0; i < combinations.length; i += 2) {
            Location loc = new Location(myR + combinations[i], myC + combinations[i + 1]);
            if (isInBounds(loc)) {
                temp.add(new Move(pieceLoc, loc, false, board));
                ret.add(temp);
            }
            temp = new ArrayList<>();
        }

        int pieceColor = getPieceColor();
        boolean[] castlingAbility = board.getCastlingAbility().getCastlingAbility(pieceColor);
        //todo check is in check
        if (!CastlingAbility.checkIfAllFalse(castlingAbility)) {
            for (int i = 0, castlingAbilityLength = castlingAbility.length; i < castlingAbilityLength; i++) {
                boolean b = castlingAbility[i];
                if (!b) continue;
                temp = new ArrayList<>();
                SpecialMoveType type = i == KING_SIDE ? SpecialMoveType.SHORT_CASTLE : SpecialMoveType.LONG_CASTLE;
                int num = type == SpecialMoveType.SHORT_CASTLE ? -1 : 1;
                Location rookLoc = getRookHomeLoc(pieceColor, i);

                Location kingMiddleLoc = new Location(myR, myC - num);
                Location kingFinalLoc = new Location(myR, myC - (num * 2));
                Location rookFinalLoc = new Location(myR, myC - num);
                ArrayList<Location> parms = new ArrayList<>(Arrays.asList(kingMiddleLoc, kingFinalLoc, rookLoc, rookFinalLoc));
                if (type == SpecialMoveType.LONG_CASTLE)
                    parms.add(new Location(myR, myC - (num * 3)));//rook middle loc
                if (!batchCheckBoundsAndEmpty(parms, Castling.ROOK_STARTING_LOC, board))
                    continue;
                temp.add(new Castling(new Move(pieceLoc, kingFinalLoc, false, board), type, parms));

                ret.add(temp);

            }
        }
        return ret;
    }

    private boolean batchCheckBoundsAndEmpty(ArrayList<Location> list, int notEmptyIndex, Board board) {
        for (int i = 0; i < list.size(); i++) {
            Location loc = list.get(i);
            if (!isInBounds(loc) || (!board.isSquareEmpty(loc) && notEmptyIndex != i))
                return false;
        }
        return true;
    }
}
