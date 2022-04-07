package ver14.SharedClasses.DBActions.Arg;

import java.io.Serializable;
import java.util.ArrayList;

public class Config<V> implements Serializable {
    public final boolean canUseDefault;
    public final String description;
    private final Described<V> defaultValue;
    private final ArrayList<Described<V>> valuesSuggestion;

    public Config() {
        this(null);
    }

    public Config(String description) {
        this(description, false, null);
    }

    public Config(String description, boolean canUseDefault, Described<V> defaultValue) {
        this.canUseDefault = canUseDefault;
        this.defaultValue = defaultValue;
        this.description = description;
        this.valuesSuggestion = new ArrayList<>();
    }


    public Config(String description, V defVal) {
        this(description, defVal, defVal + "");
    }

    public Config(String description, V defVal, String defDesc) {
        this(description, new Described<>(defVal, defDesc));
    }

    public Config(String description, Described<V> defaultValue) {
        this(description, true, defaultValue);
    }

    public void addSuggestion(Described<V> suggestion) {
        valuesSuggestion.add(suggestion);
    }

    public ArrayList<Described<V>> getValuesSuggestion() {
        return valuesSuggestion;
    }

    public Described<V> getDescribedDefault() {
        return defaultValue;
    }

    public V getDefault() {
        return defaultValue.obj();
    }

    @Override
    public String toString() {
        return "Enter " + (canUseDefault ? (description + getDefaultDesc()) : description);
    }

    public String getDefaultDesc() {
        return (canUseDefault ? "\nor leave empty. default value is " + defaultValue.description() : "");
    }


}
