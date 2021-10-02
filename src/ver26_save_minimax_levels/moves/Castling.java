package ver26_save_minimax_levels.moves;

import ver26_save_minimax_levels.Location;

import java.util.ArrayList;

public class Castling extends Move {
    public static final int KING_MIDDLE_LOC = 0, KING_FINAL_LOC = 1, ROOK_STARTING_LOC = 2, ROOK_FINAL_LOC = 3, ROOK_MIDDLE_LOC = 4;
    public static final int KING_SIDE = 0, QUEEN_SIDE = 1;
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
        moveAnnotation.overrideEverythingButGameStatus(getCastlingString());
    }

    public Castling(Castling move) {
        super(move);
        locs = move.locs;
        side = move.side;
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
        return side == KING_SIDE;
    }

}
