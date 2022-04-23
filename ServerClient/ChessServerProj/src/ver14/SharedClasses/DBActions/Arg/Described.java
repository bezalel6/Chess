package ver14.SharedClasses.DBActions.Arg;

import java.io.Serializable;

/*
 * Described
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * Described -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * Described -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public record Described<T>(T obj, String description) implements Serializable {
    public Described(T obj) {
        this(obj, obj + "");
    }

    public static <T> Described<T> d(T obj, String description) {
        return new Described<>(obj, description);
    }
}
