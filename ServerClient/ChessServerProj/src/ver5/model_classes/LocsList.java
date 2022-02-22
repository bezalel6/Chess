package ver5.model_classes;

import ver5.SharedClasses.Hashable;
import ver5.SharedClasses.Location;

import java.util.ArrayList;

public class LocsList extends ArrayList<Location> implements Hashable {

    public LocsList() {
    }

    public LocsList(LocsList other) {
        super(other);
    }
}
