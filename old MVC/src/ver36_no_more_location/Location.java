package ver36_no_more_location;

import ver36_no_more_location.view_classes.View;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public enum Location {
    SQ_A1, SQ_B1, SQ_C1, SQ_D1, SQ_E1, SQ_F1, SQ_G1, SQ_H1,
    SQ_A2, SQ_B2, SQ_C2, SQ_D2, SQ_E2, SQ_F2, SQ_G2, SQ_H2,
    SQ_A3, SQ_B3, SQ_C3, SQ_D3, SQ_E3, SQ_F3, SQ_G3, SQ_H3,
    SQ_A4, SQ_B4, SQ_C4, SQ_D4, SQ_E4, SQ_F4, SQ_G4, SQ_H4,
    SQ_A5, SQ_B5, SQ_C5, SQ_D5, SQ_E5, SQ_F5, SQ_G5, SQ_H5,
    SQ_A6, SQ_B6, SQ_C6, SQ_D6, SQ_E6, SQ_F6, SQ_G6, SQ_H6,
    SQ_A7, SQ_B7, SQ_C7, SQ_D7, SQ_E7, SQ_F7, SQ_G7, SQ_H7,
    SQ_A8, SQ_B8, SQ_C8, SQ_D8, SQ_E8, SQ_F8, SQ_G8, SQ_H8,
    SQ_NONE;
    private static final Map<Integer, Location> map = new HashMap<>();

    public static int NUM_OF_SQUARES = 64;

    static {
        for (Location p : values()) {
            map.put(p.asInt(), p);
        }
    }

    public static ArrayList<Location> allLocs() {
        return Arrays.stream(values())
                .filter(location -> location.asInt() < NUM_OF_SQUARES)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static int row(Location loc) {
        return loc.ordinal() >> 3;
    }

    public static int col(Location loc) {
        return loc.ordinal() & 7;
    }

    public static Location getLoc(Point point, int width, int height, View view) {
        if (point != null) {
            int x = point.x;
            int y = point.y;
            int divYWidth = width / 8;
            int divXHeight = height / 8;
            int col = x / divYWidth;
            int row = y / divXHeight;
//            !flip bc normally when white is on the bottom you have to flip
            if (view.isBoardFlipped()) {
                row = getFlipped(row);
                col = getFlipped(col);
            }
            return getLoc(row, col);
        }
        return null;
    }

    public static Location getLoc(Location loc, int add) {
        return getLoc(loc.asInt() + add);
    }

    public static Location getLoc(int row, int col) {
        return getLoc(row, col, false);
    }

    public static Location getLoc(int row, int col, boolean flip) {
        if (row > Controller.ROWS || row < 0 || col > Controller.COLS || col < 0)
            return null;
        if (flip) {
            row = getFlipped(row);
            col = getFlipped(col);
        }
        return valueOf(row * 8 + col);
    }

    public static Location getLoc(int loc) {
        return isInBounds(loc) ? valueOf(loc) : null;
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

    public static boolean isInBounds(Location loc) {
        return loc != null && isInBounds(loc.asInt());
    }

    public static boolean isInBounds(int loc) {
        return loc < NUM_OF_SQUARES && loc >= 0;
    }

    public static Location valueOf(int loc) {
        return map.get(loc);
    }

    public static int getFlipped(int num) {
        return Math.abs(num - 7);
    }

    public Location shift(int shiftBy, boolean shiftRight) {
        if (shiftRight) {
            return valueOf(asInt() >> shiftBy);
        }
        return valueOf(asInt() << shiftBy);
    }

    public int asInt() {
        return getRow() * 8 + getCol();
    }

    public int getRow() {
        return row(this);
    }

    public int getCol() {
        return col(this);
    }

    public String getColString() {
        return Character.toString((char) (getCol() + 'a'));
    }

    public String getRowString() {
        return (getFlipped(getRow()) + 1) + "";
    }

    @Override
    public String toString() {
        return getColString() + getRowString();
    }

    public int getMaxDistance(Location other) {
        return other == null ? -1 : Math.max(Math.abs(getRow() - other.getRow()), Math.abs(getCol() - other.getCol()));
    }
}
