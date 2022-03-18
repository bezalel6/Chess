package ver14.view.Board;

import ver14.SharedClasses.Game.Location;

public class ViewLocation {
    public final Location originalLocation;
    public final Location viewLocation;

    public ViewLocation(Location originalLocation) {
        this.originalLocation = originalLocation;
//        this.viewLocation = originalLocation.flip();
//        this.viewLocation = Location.getLoc(Location.flip(originalLocation.row), Location.flip(originalLocation.col));
        this.viewLocation = originalLocation;
    }
}
