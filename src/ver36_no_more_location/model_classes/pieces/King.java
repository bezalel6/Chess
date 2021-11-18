package ver36_no_more_location.model_classes.pieces;

import ver36_no_more_location.Location;
import ver36_no_more_location.Player;
import ver36_no_more_location.model_classes.CastlingAbility;
import ver36_no_more_location.model_classes.moves.Castling;
import ver36_no_more_location.model_classes.moves.Move;

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
    private static final ArrayList<ArrayList<Move>>[][][] preCalculatedMoves = calc();
    public static int worth = 200;

    public King(Location loc, int pieceColor) {
        super(loc, pieceColor, KING, "K");
    }

    public King(Piece other) {
        this(other.getStartingLoc(), other.getPieceColor());
        setLoc(other.getLoc());
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

    public static Location getRookHomeLoc(int player, int side) {
        return new Location(STARTING_ROW[player], 7 * ((side + 1) % 2));
    }

    private static ArrayList<ArrayList<Move>> generateMoves(Location movingFrom, int player) {
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
        if (movingFrom.getRow() == STARTING_ROW[player])
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

    public static ArrayList<ArrayList<Move>> getKingMoves(Location movingFrom, int player) {
        return preCalculatedMoves[player][movingFrom.getRow()][movingFrom.getCol()];
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves() {
        return getKingMoves(getLoc(), getPieceColor());
    }


}
