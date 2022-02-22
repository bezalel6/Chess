package ver7_without_path;

import ver7_without_path.types.Piece;

/**
 * ver1.Location - המחלקה מגדירה את הטיפוס מיקום בלוח
 * 17.11.2020
 * ilan
 */
public class Location {
    // תכונות הם משתנים שמגדירים איפה ישמרו הנתונים של הטיפוס
    private int row;    // נשמור את מספר השורה
    private int col;    // נשמור את מספר העמודה
    // פעולות

    // constructor פעולה בונה
    public Location(int r, int c) {
        row = r;
        col = c;
    }

    public Location(String str) {
        row = str.charAt(1) - '0' - 1;
        if (Piece.isWhitePerspective)
            row = Math.abs(row - 7);
        col = str.charAt(0) - 'a';
    }

    // default constructor פעולה בונה ריקה
    public Location() {
        row = 0;
        col = 0;
    }

    public boolean isEqual(Location compareTo) {
        return compareTo == null ? null : row == compareTo.getRow() && col == compareTo.getCol();
    }

    // פעולה מאחזרת את ערך השורה
    public int getRow() {
        return row;
    }

    public void setRow(int num) {
        row = num;
    }

    // פעולה מאחזרת את ערך העמודה של המיקום
    public int getCol() {
        return col;
    }

    public void setCol(char num) {
        col = num;
    }

    public String getStr() {
        String ret = (char) (col + 'a') + "";

        if (Piece.isWhitePerspective)
            ret += "" + (Math.abs(row - 7) + 1);
        else
            ret += "" + (row + 1);

        return ret;
    }

    // הפעולה מחזירה מחרוזת המתארת את מצב העצם
    @Override
    public String toString() {
        return getStr();
    }

}
