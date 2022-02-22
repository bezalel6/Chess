package ver8.SharedClasses.messages;

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
    UPDATE_SYNCED_LIST,
    INTERRUPT,
    IS_ALIVE,
    ALIVE;

    public static void main(String[] args) {
        for (MessageType t : values())
            System.out.print(t.name() + ",");
    }

    //    should stop and wait for handling to finish before listening to the next message
    public boolean shouldBlock() {
        return switch (this) {
            case INIT_GAME, UPDATE_BY_MOVE, IS_ALIVE, ALIVE -> true;
            default -> false;
        };
    }

    public boolean isGameProgression() {
        return switch (this) {
            case OFFER_DRAW, USERNAME_AVAILABILITY, PLAYERS_STATISTICS, UPDATE_SYNCED_LIST, INTERRUPT, IS_ALIVE, ALIVE -> false;
            default -> true;
        };
    }
}
