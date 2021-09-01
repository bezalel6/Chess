package ver22_eval_captures.moves;

import ver22_eval_captures.Location;

import java.util.ArrayList;

public class Castling extends Move {
    public static final int KING_MIDDLE_LOC = 0, KING_FINAL_LOC = 1, ROOK_STARTING_LOC = 2, ROOK_FINAL_LOC = 3, ROOK_MIDDLE_LOC = 4;

    public static final int KING_SIDE = 0, QUEEN_SIDE = 1;


    private Location[] locs;
    private int side;

    public Castling(Move move, int side, ArrayList<Location> parms) {
        super(move);
        this.side = side;
        locs = new Location[parms.size()];
        for (int i = 0; i < parms.size(); i++) {
            locs[i] = parms.get(i);
        }
    }

    public Castling(Castling move) {
        super(move);
        locs = move.locs;
        side = move.side;
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

    public String getAnnotation() {
        return isKingSide() ? "O-O" : "O-O-O";
    }

    @Override
    public String toString() {
        return getAnnotation();
    }
}
