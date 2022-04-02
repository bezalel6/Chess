package ver14.SharedClasses.Game;

import ver14.SharedClasses.Game.moves.Direction;

import java.util.*;
import java.util.stream.Collectors;

public enum Location {
    A8, B8, C8, D8, E8, F8, G8, H8,
    A7, B7, C7, D7, E7, F7, G7, H7,
    A6, B6, C6, D6, E6, F6, G6, H6,
    A5, B5, C5, D5, E5, F5, G5, H5,
    A4, B4, C4, D4, E4, F4, G4, H4,
    A3, B3, C3, D3, E3, F3, G3, H3,
    A2, B2, C2, D2, E2, F2, G2, H2,
    A1, B1, C1, D1, E1, F1, G1, H1,
    NONE;

    public static final int
            A = A1.col,
            B = B1.col,
            C = C1.col,
            D = D1.col,
            E = E1.col,
            F = F1.col,
            G = G1.col,
            H = H1.col;

    public static final boolean flip_fen_locs = false;
    public static final boolean flip_fen_load_locs = false;
    public static final ArrayList<Location> ALL_LOCS;
    public static final int NUM_OF_SQUARES = 64;

    public static final int KING_STARTING_COL = E1.col;

    public static final int WHITE_STARTING_ROW = E1.row;
    public static final int BLACK_STARTING_ROW = flip(WHITE_STARTING_ROW);
    public static final int WHITE_DIFF = WHITE_STARTING_ROW > BLACK_STARTING_ROW ? -1 : 1;
    public static final int BLACK_DIFF = -WHITE_DIFF;
    //    ahhhhhh
    public static final PlayerColor normalPerspective = PlayerColor.WHITE;
    private static final Map<Integer, Location> map = new HashMap<>();
    private static final long whiteSquares = 0x55aa55aa55aa55aaL;
    private static final long blackSquares = ~whiteSquares;

    static {
        for (Location p : values()) {
            map.put(p.asInt, p);
        }
        ALL_LOCS = Arrays.stream(values())
                .filter(location -> location != NONE)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public final long asLong;
    public final int asInt;
    public final int row;
    public final int col;
    public final int viewRow;
    public final int viewCol;

    Location() {
//        int matRow = row(this);

        this.row = (row(this));
        this.col = (col(this));

        this.viewRow = flip(row);
        this.viewCol = flip(col);

        this.asInt = this.row * 8 + this.col;
        this.asLong = 1L << this.asInt;
    }
//    public final int matRow

    private static int row(Location loc) {
        return loc.ordinal() >> 3;
    }

    private static int col(Location loc) {
        return loc.ordinal() & 7;
    }

    public static int flip(int num) {
        return Math.abs(num - 7);
    }

    public static void main(String[] args) {
        System.out.println(locsMatStr());
    }

    public static String locsMatStr() {
        StringBuilder str = new StringBuilder();
        for (Location[] row : locsMat()) {
            for (Location loc : row) {
                str.append(loc.matrixStr());
            }
            str.append("\n");
        }
        return str.toString();
    }

    public static Location[][] locsMat() {
        Location[][] mat = new Location[8][8];
        for (Location loc : ALL_LOCS) {
            mat[loc.row][loc.col] = loc;
        }
        return mat;
    }

    public String matrixStr() {
        return " %s: %d,%d ".formatted(name(), row, col);
    }

    public static String createMatIndicesStr() {
        StringBuilder bldr = new StringBuilder();
        for (Location loc : ALL_LOCS) {
            if (loc.col % 8 == 0)
                bldr.append("\n");
            bldr.append(loc.matrixStr());
        }
        return bldr.toString();
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
        return getLoc(loc.asInt + add);
    }

    public static Location getLoc(int loc) {
        return isInBounds(loc) ? valueOf(loc) : null;
    }

    public static boolean isInBounds(int loc) {
        return loc < NUM_OF_SQUARES && loc >= 0;
    }

    public static Location valueOf(int loc) {
        return map.get(loc);
    }

    public static Location getLoc(String str) {
//        int r = Integer.parseInt(str.substring(1)) - 1;
//        int c = Integer.parseInt((str.charAt(0) - 'a') + "");
////        c = flip(c);
//        r = flip(r);
//        Location loc = getLoc(r, c);
//        if (!isInBounds(loc)) {
//            return null;
//        }
//
//        return loc;
        return valueOf(str.toUpperCase(Locale.ROOT));
    }

    public static Location getLoc(int row, int col) {
        return getLoc(row, col, false);
    }

    public static Location getLoc(int row, int col, boolean flip) {
        if (row > 8 || row < 0 || col > 8 || col < 0)
            return null;
        if (flip) {
            row = flip(row);
            col = flip(col);
        }
        return valueOf(row * 8 + col);
    }

    public static boolean isInBounds(Location loc) {
        return loc != null && isInBounds(loc.asInt);
    }

    public Location flip() {
        return getLoc(row, col, true);
    }

    public boolean isBlackSquare() {
        return !isWhiteSquare();
    }

    //todo shouldnt be !=0?
    public boolean isWhiteSquare() {
        return (whiteSquares >> asInt & 1) == 0;
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
//        return getColString() + getRowString();
    }

//    public String getColString() {
//        return Character.toString((char) ((flip_fen_locs ? flip(col) : col) + 'a'));
//    }
//
//    public String getRowString() {
//        int r = row + 1;
//        if (!flip_fen_locs) {
//            r = flip(r);
//        }
//        return r + "";
//    }

    public int getMaxDistance(Location other) {
        return other == null ? -1 : Math.max(Math.abs(row - other.row), Math.abs(col - other.col));
    }

}
