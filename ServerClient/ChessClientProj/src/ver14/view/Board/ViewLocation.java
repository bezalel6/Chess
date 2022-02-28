package ver14.view.Board;

import ver14.SharedClasses.Location;

public class ViewLocation {
    public final Location originalLocation;
    public final Location viewLocation;

    public ViewLocation(Location originalLocation) {
        this.originalLocation = originalLocation;
        this.viewLocation = Location.getLoc(Location.getFlipped(originalLocation.row), Location.getFlipped(originalLocation.col));
    }
}
