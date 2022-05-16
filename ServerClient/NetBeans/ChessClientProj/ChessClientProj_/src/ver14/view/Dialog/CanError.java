package ver14.view.Dialog;

/**
 * represents a value that under some conditions might error. NOTE: the error detection is not done using this, only the details are acquired through it.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface CanError<V> {
    /**
     * get the value of this object.
     *
     * @return the value of this object
     */
    V obj();

    /**
     * get the Error details.
     *
     * @return the error details
     */
    String errorDetails();
}
