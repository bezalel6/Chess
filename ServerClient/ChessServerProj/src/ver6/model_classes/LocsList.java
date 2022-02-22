package ver6.model_classes;

import ver6.SharedClasses.Hashable;
import ver6.SharedClasses.Location;

import java.util.ArrayList;

public class LocsList extends ArrayList<Location> implements Hashable {

    public LocsList() {
    }

    public LocsList(LocsList other) {
        super(other);
    }
}
