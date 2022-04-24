package ver14.SharedClasses.Game;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.Moves.Direction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Location - an enum consisting of 64 values representing all 64 squares on the board. used to access squares on the board
 * <br/>an enum is used for performance reasons.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum Location {
    A8, B8, C8, D8, E8, F8, G8, H8,
    A7, B7, C7, D7, E7, F7, G7, H7,
    A6, B6, C6, D6, E6, F6, G6, H6,
    A5, B5, C5, D5, E5, F5, G5, H5,
    A4, B4, C4, D4, E4, F4, G4, H4,
    A3, B3, C3, D3, E3, F3, G3, H3,
    A2, B2, C2, D2, E2, F2, G2, H2,
    A1, B1, C1, D1, E1, F1, G1, H1;

    public static final int
            A = A1.col,
            B = B1.col,
            C = C1.col,
            D = D1.col,
            E = E1.col,
            F = F1.col,
            G = G1.col,
            H = H1.col;

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

        ALL_LOCS = Arrays.stream(values())
                .collect(Collectors.toCollection(ArrayList::new));
        for (Location p : ALL_LOCS) {
            map.put(p.asInt, p);
        }
    }

    /**
     * a long value with a bit set on this location
     */
    public final long asLong;
    /**
     * the calculated index
     */
    public final int asInt;
    /**
     * this location's row
     */
    public final int row;
    /**
     * this location's column
     */
    public final int col;

    /**
     * Instantiates a new Location.
     */
    Location() {
        this.row = ordinal() >> 3;
        this.col = ordinal() & 7;

        this.asInt = this.row * 8 + this.col;
        this.asLong = 1L << this.asInt;
    }

    //region matrix locations

    /**
     * Locs mat str string.
     *
     * @return the string
     */
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

    /**
     * Locs mat location [ ] [ ].
     *
     * @return the location [ ] [ ]
     */
    public static Location[][] locsMat() {
        Location[][] mat = new Location[8][8];
        for (Location loc : ALL_LOCS) {
            mat[loc.row][loc.col] = loc;
        }
        return mat;
    }

    /**
     * Matrix str string.
     *
     * @return the string
     */
    public String matrixStr() {
        return " %s: %d,%d ".formatted(name(), row, col);
    }

    /**
     * Create mat indices str string.
     *
     * @return the string
     */
    public static String createMatIndicesStr() {
        StringBuilder bldr = new StringBuilder();
        for (Location loc : ALL_LOCS) {
            if (loc.col % 8 == 0)
                bldr.append("\n");
            bldr.append(loc.matrixStr());
        }
        return bldr.toString();
    }
//endregion

    /**
     * Gets the location relative to loc in the direction
     *
     * @param loc       the loc
     * @param direction the direction
     * @return the location if the calculated index is inside the bounds(0...63). null otherwise
     */
    public static Location getLoc(Location loc, Direction direction) {
        return getLoc(loc, 1, direction);
    }

    /**
     * Gets the location relative to loc in the direction given and the distance is determined by the numOfMult
     *
     * @param loc       the loc
     * @param numOfMult the num of mult
     * @param direction the direction
     * @return the location if the calculated index is inside the bounds(0...63). null otherwise
     */
    public static Location getLoc(Location loc, int numOfMult, Direction direction) {
        int add = direction.offset;
        add *= numOfMult;
        return getLoc(loc, add);
    }

    /**
     * Gets the location that is exactly add squares from loc
     * NOTE: add should be in bitboard format
     *
     * @param loc the loc
     * @param add the number of squares to add
     * @return the location if the calculated index is inside the bounds(0...63). null otherwise
     * @see ver14.Model.Bitboard
     */
    public static Location getLoc(Location loc, int add) {
        return getLoc(loc.asInt + add);
    }

    /**
     * Gets location corresponding to the locIndex provided (0..63)
     *
     * @param locIndex the locIndex
     * @return the location if the provided index is inside the bounds(0...63). null otherwise
     */
    public static Location getLoc(int locIndex) {
        return valueOf(locIndex);
    }

    /**
     * Value of location.
     *
     * @param locIndex the locIndex
     * @return the location if the provided index is inside the bounds(0...63). null otherwise
     */
    public static Location valueOf(int locIndex) {
        return map.get(locIndex);
    }

    /**
     * gets the location corresponding to the provided location string.
     *
     * @param str the square's coordinate according to the Algebraic notation
     * @return the loc if given str is valid. null otherwise
     * @see <a href="https://en.wikipedia.org/wiki/Algebraic_notation_(chess)">...</a>
     */
    public static Location getLoc(String str) {
        return valueOf(str.toUpperCase(Locale.ROOT));
    }

    /**
     * Gets loc.
     *
     * @param row the row
     * @param col the col
     * @return the loc
     */
    public static Location getLoc(int row, int col) {
        return getLoc(row, col, false);
    }

    /**
     * Gets loc.
     *
     * @param row  the row
     * @param col  the col
     * @param flip the flip
     * @return the loc
     */
    public static Location getLoc(int row, int col, boolean flip) {
        if (row > 8 || row < 0 || col > 8 || col < 0)
            return null;
        if (flip) {
            row = flip(row);
            col = flip(col);
        }
        return valueOf(row * 8 + col);
    }

    /**
     * Flip int.
     *
     * @param num the num
     * @return the int
     */
    public static int flip(int num) {
        return Math.abs(num - 7);
    }

    /**
     * Is in bounds boolean.
     *
     * @param loc the loc
     * @return the boolean
     */
    public static boolean isInBounds(Location loc) {
        return loc != null;
    }

    /**
     * Gets col string.
     *
     * @return the col string
     */
    public String getColString() {
        return name().substring(0, 1);
    }

    /**
     * Flip location.
     *
     * @return the location
     */
    public Location flip() {
        return getLoc(row, col, true);
    }

    /**
     * Is black square boolean.
     *
     * @return the boolean
     */
    public boolean isBlackSquare() {
        return !isWhiteSquare();
    }

    /**
     * Is white square boolean.
     *
     * @return the boolean
     */
//todo shouldnt be !=0?
    public boolean isWhiteSquare() {
        return (whiteSquares >> asInt & 1) == 0;
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

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
//        return getColString() + getRowString();
    }

    /**
     * Gets max distance.
     *
     * @param other the other
     * @return the max distance
     */
    public int getMaxDistance(Location other) {
        return other == null ? -1 : Math.max(Math.abs(row - other.row), Math.abs(col - other.col));
    }

    /**
     * Gets row string.
     *
     * @return the row string
     */
    public String getRowString() {
        return name().substring(1);
    }

    /**
     * Hash int.
     *
     * @param pieceType the piece type
     * @return the int
     */
    public int hash(PieceType pieceType) {
        return Objects.hash(asInt, pieceType.asInt);
    }
}
