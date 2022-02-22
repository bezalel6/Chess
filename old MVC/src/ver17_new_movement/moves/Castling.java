package ver17_new_movement.moves;

import ver17_new_movement.Location;
import ver17_new_movement.types.Rook;

import java.util.ArrayList;

public class Castling extends SpecialMove {
    public static final int KING_MIDDLE_LOC = 0;
    public static final int KING_FINAL_LOC = 1;
    public static final int ROOK_STARTING_LOC = 2;
    public static final int ROOK_FINAL_LOC = 3;
    public static final int ROOK_MIDDLE_LOC = 4;

    private Location[] locs;

    public Castling(Move move, SpecialMoveType moveType, ArrayList<Location> parms) {
        super(move, moveType);
        locs = new Location[parms.size()];
        for (int i = 0; i < parms.size(); i++) {
            locs[i] = parms.get(i);
        }
    }

    public Location[] getCastlingLocs() {
        return locs;
    }

    public boolean isKingSide() {
        return getMoveType() == SpecialMoveType.SHORT_CASTLE;
    }

    public String getAnnotation() {
        return isKingSide() ? "O-O" : "O-O-O";
    }

    @Override
    public String toString() {
        return "Castling{" +

                '}';
    }
}
