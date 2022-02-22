package ver8.view.Graph;

import java.util.Locale;

public enum GraphElementType {
    GREEN, RED, YELLOW;

    public String iconName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
