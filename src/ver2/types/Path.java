package ver2.types;

import ver2.Location;

public class Path {
    private Location loc;
    private boolean isCapturing;

    public Path(Location loc, boolean isCapturing) {
        this.loc = loc;
        this.isCapturing = isCapturing;
    }

    public Location getLoc() {
        return loc;
    }

    public boolean isCapturing() {
        return isCapturing;
    }
}