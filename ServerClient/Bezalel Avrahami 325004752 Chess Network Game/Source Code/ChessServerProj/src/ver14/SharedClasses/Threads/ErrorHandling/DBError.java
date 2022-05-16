package ver14.SharedClasses.Threads.ErrorHandling;

/**
 * represents a database error.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class DBError extends MyError {
    /**
     * Instantiates a new Db err.
     *
     * @param throwable the throwable
     */
    public DBError(Throwable throwable) {
        super(throwable);
    }

    /**
     * Instantiates a new Db err.
     *
     * @param message the message
     * @param cause   the cause
     */
    public DBError(String message, Throwable cause) {
        super(message, cause);
    }
}
