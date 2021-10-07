package ver27_transpositions;

import ver27_transpositions.view_classes.View;

import java.awt.*;

import static ver27_transpositions.Controller.COLS;
import static ver27_transpositions.Controller.ROWS;

public class Location {

    private int row;
    private int col;

    public Location(int r, int c) {
        this(r, c, false);
    }

    public Location(Point point, int width, int height, View view) {
        if (point != null) {
            int x = point.x;
            int y = point.y;
            int divYWidth = width / 8;
            int divXHeight = height / 8;
            col = x / divYWidth;
            row = y / divXHeight;
//            !flip bc normally when white is on the bottom you have to flip
            if (!view.isBoardFlipped()) {
                row = getFlipped(row);
                col = getFlipped(col);
            }
        }
    }

    public Location(int r, int c, boolean flip) {
        if (flip) {
            row = getFlipped(r);
            col = getFlipped(c);
        } else {
            row = r;
            col = c;
        }
    }

    public Location(String str) {
        col = Integer.parseInt((str.charAt(0) - 'a') + "");
        row = Integer.parseInt(str.substring(1)) - 1;
    }

    public Location(Location other) {
        row = other.row;
        col = other.col;
    }

    public static Location flipLocation(Location loc) {
        return new Location(getFlipped(loc.row), loc.col);
    }

    public static int getFlipped(int num) {
        return Math.abs(num - 7);
    }

    public static boolean batchCheckBounds(Location[] list) {
        for (Location loc : list) {
            if (!loc.isInBounds())
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return row == location.row && col == location.col;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt((row + 1) + "" + (col + 1));
    }

    // פעולה מאחזרת את ערך השורה
    public int getRow() {
        return row;
    }

    public void setRow(int num) {
        row = num;
    }

    public String getColString() {
        return Character.toString((char) (col + 'a'));
    }

    public int getCol() {
        return col;
    }

    public void setCol(char num) {
        col = num;
    }

    public String getNumValues() {
        return "[" + row + "]" + "[" + col + "]";
    }

    @Override
    public String toString() {
        String ret = getColString() + "";
        ret += (row + 1) + "";
        return ret;
    }

    public boolean isInBounds() {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }
}
