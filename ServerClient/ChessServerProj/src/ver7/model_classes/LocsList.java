package ver7.model_classes;

import ver7.SharedClasses.Hashable;
import ver7.SharedClasses.Location;

import java.util.ArrayList;

public class LocsList extends ArrayList<Location> implements Hashable {

    public LocsList() {
    }

    public LocsList(LocsList other) {
        super(other);
    }
}
