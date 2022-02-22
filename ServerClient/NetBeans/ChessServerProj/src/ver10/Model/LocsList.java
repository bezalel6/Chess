package ver10.Model;

import ver10.SharedClasses.Hashable;
import ver10.SharedClasses.Location;

import java.util.ArrayList;

public class LocsList extends ArrayList<Location> implements Hashable {

    public LocsList() {
    }

    public LocsList(LocsList other) {
        super(other);
    }
}
