package ver5.SharedClasses.messages;

public enum MessageType {
    LOGIN,
    RESIGN,
    ADD_TIME,
    OFFER_DRAW,
    WELCOME_MESSAGE,
    GET_GAME_SETTINGS,
    WAIT_FOR_MATCH,
    INIT_GAME,
    WAIT_TURN,
    GET_MOVE,
    UPDATE_BY_MOVE,
    GAME_OVER,
    ERROR,
    QUESTION,
    BYE,
    USERNAME_AVAILABILITY,
    PLAYERS_STATISTICS,
    RESUMABLE_GAMES,
    JOINABLE_GAMES,
    INTERRUPT(true),
    STOP_READ(true),
    IS_ALIVE(true),
    ALIVE(true);
    public final boolean ignore;

    MessageType() {
        this(false);
    }

    MessageType(boolean ignore) {
        this.ignore = ignore;
    }
}
