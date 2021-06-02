package ver5.types;

import ver5.Location;

public class Path {
    private Location loc;
    private boolean isCapturing, isCastlingKingPath=false,isKingDestination=false,isPromoting=false;
    public Path(Location loc, boolean isCapturing) {
        this.loc = loc;
        this.isCapturing = isCapturing;
    }

    public Path(Location loc, boolean isCapturing, boolean isPromoting) {
        this.loc = loc;
        this.isCapturing = isCapturing;
        this.isPromoting = isPromoting;
    }

    public Path(Location loc, boolean isCapturing, boolean isCastlingKingPath, boolean isKingDestination) {
        this.loc = loc;
        this.isCapturing = isCapturing;
        this.isCastlingKingPath = isCastlingKingPath;
        this.isKingDestination = isKingDestination;
    }
    public void setPromoting()
    {
        isPromoting=true;
    }

    public boolean isPromoting() {
        return isPromoting;
    }

    public boolean isKingDestination() {
        return isKingDestination;
    }

    public boolean isCastlingKingPath() {
        return isCastlingKingPath;
    }

    public Location getLoc() {
        return loc;
    }

    public boolean isCapturing() {
        return isCapturing;
    }

    @Override
    public String toString() {
        return "Path{" +
                "loc=" + loc +
                ", isCapturing=" + isCapturing +
                ", isPromoting=" + isPromoting +
                '}';
    }
}