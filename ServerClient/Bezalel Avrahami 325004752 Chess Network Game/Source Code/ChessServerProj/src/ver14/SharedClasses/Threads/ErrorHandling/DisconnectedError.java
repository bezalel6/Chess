package ver14.SharedClasses.Threads.ErrorHandling;

/**
 * represents a disconnection error.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class DisconnectedError extends MyError {
    /**
     * Instantiates a new Disconnected error.
     */
    public DisconnectedError() {
        this("");
    }

    /**
     * Instantiates a new Disconnected error.
     *
     * @param message the message
     */
    public DisconnectedError(String message) {
        super(message);
    }

    /**
     * Instantiates a new Disconnected error.
     *
     * @param throwable the throwable
     */
    public DisconnectedError(Throwable throwable) {
        super(throwable);
    }
}
