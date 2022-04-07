package ver14.SharedClasses.DBActions.Arg;

import java.io.Serializable;

public record Described<T>(T obj, String description) implements Serializable {
    public Described(T obj) {
        this(obj, obj + "");
    }

    public static <T> Described<T> d(T obj, String description) {
        return new Described<>(obj, description);
    }
}
