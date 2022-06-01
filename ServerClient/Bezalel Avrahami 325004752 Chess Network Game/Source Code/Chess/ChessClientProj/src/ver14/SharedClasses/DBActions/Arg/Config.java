package ver14.SharedClasses.DBActions.Arg;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * an {@link Arg}'s configuration consisting of:<br/>
 * a description: describing the argument's requirements.<br/>
 * a default {@link Described<V>} value (optional)<br/>
 * a list of {@link Described<V>} suggestions (optional)<br/>
 *
 * @param <V> the type of the argument
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Config<V> implements Serializable {
    /**
     * The Can use default.
     */
    public final boolean canUseDefault;
    /**
     * The Description.
     */
    public final String description;
    /**
     * The Default value.
     */
    private final Described<V> defaultValue;
    /**
     * The Values suggestion.
     */
    private final ArrayList<Described<V>> valuesSuggestion;

    /**
     * Instantiates a new Config.
     */
    public Config() {
        this(null);
    }

    /**
     * Instantiates a new Config.
     *
     * @param description the description
     */
    public Config(String description) {
        this(description, false, null);
    }

    /**
     * Instantiates a new Config.
     *
     * @param description   the description
     * @param canUseDefault the can use default
     * @param defaultValue  the default value
     */
    public Config(String description, boolean canUseDefault, Described<V> defaultValue) {
        this.canUseDefault = canUseDefault;
        this.defaultValue = defaultValue;
        this.description = description;
        this.valuesSuggestion = new ArrayList<>();
    }


    /**
     * Instantiates a new Config.
     *
     * @param description the description
     * @param defVal      the def val
     */
    public Config(String description, V defVal) {
        this(description, defVal, defVal + "");
    }

    /**
     * Instantiates a new Config.
     *
     * @param description the description
     * @param defVal      the def val
     * @param defDesc     the def desc
     */
    public Config(String description, V defVal, String defDesc) {
        this(description, new Described<>(defVal, defDesc));
    }

    /**
     * Instantiates a new Config.
     *
     * @param description  the description
     * @param defaultValue the default value
     */
    public Config(String description, Described<V> defaultValue) {
        this(description, true, defaultValue);
    }

    /**
     * Add suggestion.
     *
     * @param suggestion the suggestion
     */
    public void addSuggestion(Described<V> suggestion) {
        valuesSuggestion.add(suggestion);
    }

    /**
     * Gets values suggestion.
     *
     * @return the values suggestion
     */
    public ArrayList<Described<V>> getValuesSuggestion() {
        return valuesSuggestion;
    }

    /**
     * Gets described default.
     *
     * @return the described default
     */
    public Described<V> getDescribedDefault() {
        return defaultValue;
    }

    /**
     * Gets default.
     *
     * @return the default
     */
    public V getDefault() {
        return defaultValue.obj();
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "Enter " + (canUseDefault ? (description + getDefaultDesc()) : description);
    }

    /**
     * Gets default desc.
     *
     * @return the default desc
     */
    public String getDefaultDesc() {
        return (canUseDefault ? "\nor leave empty. default value is " + defaultValue.description() : "");
    }


}
