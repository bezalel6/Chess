package ver14.SharedClasses.Networking.Messages;


/**
 * Message type.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum MessageType {
    /**
     * Login message type.
     */
    LOGIN,
    /**
     * Resign message type.
     */
    RESIGN,
    /**
     * Add time message type.
     */
    ADD_TIME,
    /**
     * Welcome message message type.
     */
    WELCOME_MESSAGE,
    /**
     * Get game settings message type.
     */
    GET_GAME_SETTINGS,
    /**
     * Wait for match message type.
     */
    WAIT_FOR_MATCH,
    /**
     * Init game message type.
     */
    INIT_GAME(true),
    /**
     * Wait turn message type.
     */
    WAIT_TURN(true),
    /**
     * Get move message type.
     */
    GET_MOVE(true),
    /**
     * Throw error message type.
     */
    THROW_ERROR,
    /**
     * Update by move message type.
     */
    UPDATE_BY_MOVE(true),
    /**
     * Game over message type.
     */
    GAME_OVER,
    /**
     * Error message type.
     */
    ERROR,
    /**
     * Question message type.
     */
    QUESTION,
    /**
     * Bye message type.
     */
    BYE(true),
    /**
     * Username availability message type.
     */
    USERNAME_AVAILABILITY,
    /**
     * Db request message type.
     */
    DB_REQUEST,
    /**
     * Db response message type.
     */
    DB_RESPONSE,
    /**
     * Update synced list message type.
     */
    UPDATE_SYNCED_LIST,
    /**
     * Cancel question message type.
     */
    CANCEL_QUESTION,
    /**
     * Interrupt message type.
     */
    INTERRUPT;
    /**
     * The Chronological importance.
     */
    public final boolean chronologicalImportance;

    /**
     * Instantiates a new Message type.
     */
    MessageType() {
        this(false);
    }

    /**
     * Instantiates a new Message type.
     *
     * @param chronologicalImportance the chronological importance
     */
    MessageType(boolean chronologicalImportance) {
        this.chronologicalImportance = chronologicalImportance;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        for (MessageType t : values())
            System.out.print(t.name() + ",");
    }


}
