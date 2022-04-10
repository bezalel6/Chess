package ver14.SharedClasses.messages;

public enum MessageType {
    LOGIN,
    RESIGN,
    ADD_TIME,
    OFFER_DRAW,
    WELCOME_MESSAGE,
    GET_GAME_SETTINGS,
    WAIT_FOR_MATCH,
    INIT_GAME(true),
    WAIT_TURN(true),
    GET_MOVE(true),
    THROW_ERROR,
    UPDATE_BY_MOVE(true),
    GAME_OVER,
    ERROR,
    QUESTION,
    BYE,
    USERNAME_AVAILABILITY,
    DB_REQUEST,
    DB_RESPONSE,
    UPDATE_SYNCED_LIST,
    INTERRUPT,
    IS_ALIVE,
    ALIVE;
    public final boolean chronologicalImportance;

    MessageType() {
        this(false);
    }

    MessageType(boolean chronologicalImportance) {
        this.chronologicalImportance = chronologicalImportance;
    }

    public static void main(String[] args) {
        for (MessageType t : values())
            System.out.print(t.name() + ",");
    }

    //should block: init game. to make getMove wait for initialization

    public boolean hideQuestion() {
        return false;
    }
}
