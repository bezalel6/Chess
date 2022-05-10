package ver14.SharedClasses.Sync;

/**
 * represents a Synced list type.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum SyncedListType {
    /**
     * games that have been paused and may be resumed.
     */
    RESUMABLE_GAMES,
    /**
     * games a player can join.
     */
    JOINABLE_GAMES,
    /**
     * the connected users to the server.
     */
    CONNECTED_USERS,
    /**
     * the games that are being played on the server.
     */
    ONGOING_GAMES;

}
