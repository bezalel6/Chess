package ver12_myJbutton;


public class Location {

    boolean isWhiteRespective = true;
    private int row;
    private int col;

    public Location(int r, int c) {
        col = c;
        row = r;
    }

    public Location(int r, int c, boolean view) {
        col = c;
        row = getViewRow(r);
    }

    public Location(String str) {
        col = Integer.parseInt((str.charAt(0) - 'a') + "");
        row = Integer.parseInt(str.substring(1)) - 1;
    }

    public Location(Location other) {
        if (other != null) {
            row = other.row;
            col = other.col;
        }
    }

    public boolean equals(Location compareTo) {
        return compareTo == null ? null : row == compareTo.getRow() && col == compareTo.getCol();
    }

    public Location getViewLocation() {
        return new Location(getViewRow(row), col);
    }

    public void convertToModelLoc() {
        row = getViewRow(row);
    }

    public int getViewRow(int otherRow) {
        return Math.abs(otherRow - 7);
    }

    public int getViewRow() {
        return isWhiteRespective ? Math.abs(row - 7) : row;
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

    @Override
    public String toString() {
        String ret = getColString() + "";
        ret += (row + 1) + "";
        return ret;
    }

}
