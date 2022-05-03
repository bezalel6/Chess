package ver14.view.Dialog;

/**
 * represents a value, that under some conditions, might error. NOTE: the error detection is not done using this, only the details are acquired through it.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface CanError<V> {
    /**
     * Obj v.
     *
     * @return the v
     */
    V obj();

    /**
     * Error details string.
     *
     * @return the string
     */
    String errorDetails();
}
