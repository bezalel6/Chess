package ver12.SharedClasses.DBActions.Arg;

import java.io.Serializable;

public class Config<V> implements Serializable {
    public final boolean canUseDefault;
    public final String defaultValueDesc;
    public final String description;
    public final V defaultValue;

    public Config() {
        this(null);
    }

    public Config(String description) {
        this(description, false, null, null);
    }

    public Config(String description, boolean canUseDefault, V defaultValue, String defaultValueDesc) {
        this.canUseDefault = canUseDefault;
        this.defaultValue = defaultValue;
        this.defaultValueDesc = defaultValueDesc;
        this.description = description;
    }

    public Config(String description, V defaultValue) {
        this(description, defaultValue, defaultValue + "");
    }

    public Config(String description, V defaultValue, String defaultValueDesc) {
        this(description, true, defaultValue, defaultValueDesc);
    }

    public V getDefault() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return "Enter " + (canUseDefault ? (description + getDefaultDesc()) : description);
    }

    public String getDefaultDesc() {
        return (canUseDefault ? "\nor leave empty. default value is " + defaultValueDesc : "");
    }
}
