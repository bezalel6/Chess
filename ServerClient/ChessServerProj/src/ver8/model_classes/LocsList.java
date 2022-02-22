package ver8.model_classes;

import ver8.SharedClasses.Hashable;
import ver8.SharedClasses.Location;

import java.util.ArrayList;

public class LocsList extends ArrayList<Location> implements Hashable {

    public LocsList() {
    }

    public LocsList(LocsList other) {
        super(other);
    }
}
