package ver14.SharedClasses.DBActions.DBResponsePackage.Graphable;

import java.util.Locale;


public enum GraphElementType {
    GREEN, RED, YELLOW, NORMAL;

    public String iconName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
