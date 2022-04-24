package ver14.SharedClasses.DBActions.Arg;

import java.io.Serializable;


/**
 * Described represents a described object.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public record Described<T>(T obj, String description) implements Serializable {
    /**
     * Instantiates a new Described.
     *
     * @param obj the obj
     */
    public Described(T obj) {
        this(obj, obj + "");
    }

    /**
     * D described.
     *
     * @param <T>         the type parameter
     * @param obj         the obj
     * @param description the description
     * @return the described
     */
    public static <T> Described<T> d(T obj, String description) {
        return new Described<>(obj, description);
    }
}
