package ver14.SharedClasses.DBActions.Arg;

import java.io.Serializable;


/**
 * represents a described object of type {@link T}.
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
    
}
