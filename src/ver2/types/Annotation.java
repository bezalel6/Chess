package ver2.types;

import ver2.Location;

public class Annotation {
    private char col;
    private int row;

    public Annotation(char col, int row) {
        this.col = col;
        this.row = row;
    }
    public void updateLoc(Location loc)
    {
        col = (char) (loc.getCol()+'a');
        row = Math.abs(row-7);
    }
    public Location getMatLoc()
    {
        int mCol = col-'a';
        int mRow = row;
        mRow = Math.abs(mRow-8);
        return new Location(mRow,mCol);
    }

    @Override
    public String toString() {
        return "Annotation{" +
                "col=" + col +
                ", row=" + row +
                '}';
    }
}
