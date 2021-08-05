package ver14_pieceLocation;

public class Location {

    private int row;
    private int col;

    public Location(int r, int c) {
        col = c;
        row = r;
    }

    public Location(int r, int c, boolean view) {
        col = c;
        row = r;
    }

    public Location(String str) {
        col = Integer.parseInt((str.charAt(0) - 'a') + "");
        row = Integer.parseInt(str.substring(1)) - 1;
        row = Math.abs(row - 7);
    }

    public Location(Location other) {
        row = other.row;
        col = other.col;
    }

    public static Location convertToMatLoc(Location loc) {
        return new Location(Math.abs(loc.row - 7), loc.col);
    }

    public boolean equals(Location compareTo) {
        return compareTo == null ? null : row == compareTo.getRow() && col == compareTo.getCol();
    }

    // פעולה מאחזרת את ערך השורה
    public int getRow() {
        return row;
    }

    public void setRow(int num) {
        row = num;
    }

    public char getColString() {
        return (char) (col + 'a');
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

}
