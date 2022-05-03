package ver14.Model;

import ver14.SharedClasses.Game.Location;

import java.util.ArrayList;


/**
 * Locs - represents a list of locations.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Locs extends ArrayList<Location> {

    /**
     * Instantiates a new Locs.
     */
    public Locs() {
    }

    /**
     * Instantiates a new Locs.
     *
     * @param other the other
     */
    public Locs(Locs other) {
        super(other);
    }
//
//    public Location get(int i) {
////        return (Location) super.stream().toArray()[i];
//    }
}
