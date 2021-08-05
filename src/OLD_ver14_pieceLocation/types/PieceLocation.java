package OLD_ver14_pieceLocation.types;

import OLD_ver14_pieceLocation.Location;

public class PieceLocation extends Location {
    private Location loc;
    private Location matLoc;

    public PieceLocation(Location loc) {
        super(loc);
        this.loc = loc;
        matLoc = Location.convertToMatLoc(loc);
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = new Location(loc);
    }

    public Location getMatLoc() {
        return matLoc;
    }

    @Override
    public String toString() {
        return "PieceLocation{" +
                "loc=" + loc +
                ", matLoc=" + matLoc +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PieceLocation other = (PieceLocation) o;
        return other.loc.equals(this.loc) && other.matLoc.equals(this.matLoc);
    }

}
