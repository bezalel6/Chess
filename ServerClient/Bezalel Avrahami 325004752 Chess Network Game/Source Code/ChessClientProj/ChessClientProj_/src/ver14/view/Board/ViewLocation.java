package ver14.view.Board;

import ver14.SharedClasses.Game.Location;

/**
 * represents a location corrected for the shifted perspective of the view.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ViewLocation {
    /**
     * The Original location.
     */
    public final Location originalLocation;
    /**
     * The View location.
     */
    public final Location viewLocation;

    /**
     * Instantiates a new View location.
     *
     * @param originalLocation the original location
     */
    public ViewLocation(Location originalLocation) {
        this.originalLocation = originalLocation;
        this.viewLocation = originalLocation.flip();
//        this.viewLocation = originalLocation.flip();
//        this.viewLocation = Location.getLoc(Location.flip(originalLocation.row), Location.flip(originalLocation.col));
//        this.viewLocation = originalLocation;
    }

    @Override
    public String toString() {
        return "ViewLocation{" +
                "originalLocation=" + originalLocation +
                ", viewLocation=" + viewLocation +
                '}';
    }
}
