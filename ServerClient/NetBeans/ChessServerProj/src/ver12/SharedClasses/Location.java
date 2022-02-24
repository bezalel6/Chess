package ver12.SharedClasses;

//import ver12.Model.Bitboard;

import ver12.SharedClasses.moves.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public enum Location {
    A1, B1, C1, D1, E1, F1, G1, H1,
    A2, B2, C2, D2, E2, F2, G2, H2,
    A3, B3, C3, D3, E3, F3, G3, H3,
    A4, B4, C4, D4, E4, F4, G4, H4,
    A5, B5, C5, D5, E5, F5, G5, H5,
    A6, B6, C6, D6, E6, F6, G6, H6,
    A7, B7, C7, D7, E7, F7, G7, H7,
    A8, B8, C8, D8, E8, F8, G8, H8,
    NONE;
    public static final ArrayList<Location> ALL_LOCS;
    public static final int NUM_OF_SQUARES = 64;
    private static final int[] STARTING_ROW;
    private static final Map<Integer, Location> map = new HashMap<>();
    private static final long blackSquares = 0xaa55aa55aa55aa55L;
    private static final long whiteSquares = 0x55aa55aa55aa55aaL;

    static {
        for (Location p : values()) {
            map.put(p.asInt(), p);
        }
        ALL_LOCS = Arrays.stream(values())
                .filter(location -> location.asInt() < NUM_OF_SQUARES)
                .collect(Collectors.toCollection(ArrayList::new));
        STARTING_ROW = new int[PlayerColor.NUM_OF_PLAYERS];
        STARTING_ROW[PlayerColor.WHITE.asInt()] = 7;
        STARTING_ROW[PlayerColor.BLACK.asInt()] = 0;
    }

    public final long asLong;
    public final int asInt;
    public final int row;
    public final int col;

    Location() {
        this.row = row(this);
        this.col = col(this);
        this.asInt = this.row * 8 + this.col;
        this.asLong = 1L << this.asInt;
    }

    private static int row(Location loc) {
        return loc.ordinal() >> 3;
    }

    private static int col(Location loc) {
        return loc.ordinal() & 7;
    }

    public static Location getLoc(Location loc, Direction direction) {
        return getLoc(loc, 1, direction);
    }

    public static Location getLoc(Location loc, int numOfMult, Direction direction) {
        int add = direction.offset;
        add *= numOfMult;
        return getLoc(loc, add);
    }

    public static Location getLoc(Location loc, int add) {
        return getLoc(loc.asInt() + add);
    }

    public static Location getLoc(int loc) {
        return isInBounds(loc) ? valueOf(loc) : null;
    }

    public int asInt() {
        return this.asInt;
    }

    public static boolean isInBounds(int loc) {
        return loc < NUM_OF_SQUARES && loc >= 0;
    }

    public static Location valueOf(int loc) {
        return map.get(loc);
    }

    public static Location getLoc(String str) {
        int r = Integer.parseInt(str.substring(1)) - 1;
        int c = Integer.parseInt((str.charAt(0) - 'a') + "");
        r = getFlipped(r);
        Location loc = getLoc(r, c);
        if (!isInBounds(loc)) {
            return null;
        }

        return loc;
    }

    public static int getFlipped(int num) {
        return Math.abs(num - 7);
    }

    public static Location getLoc(int row, int col) {
        return getLoc(row, col, false);
    }

    public static boolean isInBounds(Location loc) {
        return loc != null && isInBounds(loc.asInt());
    }

    public static Location getLoc(int row, int col, boolean flip) {
        if (row > 8 || row < 0 || col > 8 || col < 0)
            return null;
        if (flip) {
            row = getFlipped(row);
//            col = getFlipped(col);
        }
        return valueOf(row * 8 + col);
    }

    public boolean isWhiteSquare() {
        return (whiteSquares >> asInt() & 1) == 0;
    }

    @Override
    public String toString() {
        return getColString() + getRowString();
    }

    public String getColString() {
        return Character.toString((char) (col + 'a'));
    }

    public String getRowString() {
        return (getFlipped(row) + 1) + "";
    }

    public int getMaxDistance(Location other) {
        return other == null ? -1 : Math.max(Math.abs(row - other.row), Math.abs(col - other.col));
    }

}
