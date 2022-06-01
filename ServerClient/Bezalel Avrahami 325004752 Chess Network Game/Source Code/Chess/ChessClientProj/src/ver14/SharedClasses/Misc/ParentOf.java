package ver14.SharedClasses.Misc;


/**
 * represents a Parent of a settable child.
 *
 * @param <Child> the child type
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface ParentOf<Child> {

    /**
     * Set the value of the child.
     *
     * @param value the value
     */
    void set(Child value);
}
