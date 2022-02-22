package ver3.types;

import ver3.Location;

public class Path {
    private Location loc;
    private boolean isCapturing, isCastlingKingPath = false, isKingDestination = false;

    public Path(Location loc, boolean isCapturing) {
        this.loc = loc;
        this.isCapturing = isCapturing;
    }

    public Path(Location loc, boolean isCapturing, boolean isCastlingKingPath, boolean isKingDestination) {
        this.loc = loc;
        this.isCapturing = isCapturing;
        this.isCastlingKingPath = isCastlingKingPath;
        this.isKingDestination = isKingDestination;
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
}