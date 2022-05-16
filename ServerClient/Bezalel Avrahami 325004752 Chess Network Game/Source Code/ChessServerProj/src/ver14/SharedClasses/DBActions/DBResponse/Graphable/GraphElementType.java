package ver14.SharedClasses.DBActions.DBResponse.Graphable;

import java.util.Locale;


public enum GraphElementType {
    GREEN, RED, YELLOW, NORMAL;

    public String iconName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
