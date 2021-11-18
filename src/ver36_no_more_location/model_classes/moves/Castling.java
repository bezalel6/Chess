package ver36_no_more_location.model_classes.moves;

import ver36_no_more_location.Location;
import ver36_no_more_location.model_classes.CastlingAbility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Castling extends Move {
    public static final int KING_MIDDLE_LOC = 0, KING_FINAL_LOC = 1, ROOK_STARTING_LOC = 2, ROOK_FINAL_LOC = 3, ROOK_MIDDLE_LOC = 4;
    private static final String[] CASTLING_ANNOTATION = {"O-O", "O-O-O"};
    private final Location[] locs;
    private final int side;

    public Castling(Move move, int side, ArrayList<Location> parms) {
        super(move);
        this.side = side;
        locs = new Location[parms.size()];
        for (int i = 0; i < parms.size(); i++) {
            locs[i] = parms.get(i);
        }
        intermediateMove = new BasicMove(locs[ROOK_STARTING_LOC], locs[ROOK_FINAL_LOC]);
        moveAnnotation.overrideEverythingButGameStatus(getCastlingString());
    }

    public Castling(Castling move) {
        super(move);
        locs = new Location[move.locs.length];
        Location[] locations = move.locs;
        for (int i = 0, locationsLength = locations.length; i < locationsLength; i++) {
            locs[i] = new Location(locations[i]);
        }
        side = move.side;
    }

    public List<Location> getKingPath() {
        return Arrays.asList(movingFrom, locs[KING_MIDDLE_LOC], locs[KING_FINAL_LOC]);
    }

    public String getCastlingString() {
        return CASTLING_ANNOTATION[side];
    }

    public int getSide() {
        return side;
    }

    public Location[] getCastlingLocs() {
        return locs;
    }

    public boolean isKingSide() {
        return side == CastlingAbility.KING_SIDE;
    }

}
