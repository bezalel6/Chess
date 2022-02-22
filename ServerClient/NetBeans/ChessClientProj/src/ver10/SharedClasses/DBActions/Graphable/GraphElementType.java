package ver10.SharedClasses.DBActions.Graphable;

import java.util.Locale;

public enum GraphElementType {
    GREEN, RED, YELLOW;

    public String iconName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
