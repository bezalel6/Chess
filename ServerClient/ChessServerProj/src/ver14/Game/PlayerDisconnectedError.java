package ver14.Game;

import ver14.Players.Player;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;

/**
 * an error that is thrown when a player is disconnected.<br />
 * NOTE: this error is not to be sent to the client, as it holds a reference to a {@link Player} object
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class PlayerDisconnectedError extends MyError.DisconnectedError {
    /**
     * The Disconnected player.
     */
    private final Player disconnectedPlayer;

    /**
     * Instantiates a new Player disconnected error.
     *
     * @param disconnectedPlayer the disconnected player
     */
    public PlayerDisconnectedError(Player disconnectedPlayer) {
        this.disconnectedPlayer = disconnectedPlayer;
    }

    /**
     * Create game status game status.
     *
     * @return the game status
     */
    public GameStatus createGameStatus() {
        return GameStatus.playerDisconnected(disconnectedPlayer.getPlayerColor(), disconnectedPlayer.getPartner().isAi());
    }

    /**
     * Gets disconnected player.
     *
     * @return the disconnected player
     */
    public Player getDisconnectedPlayer() {
        return disconnectedPlayer;
    }
}
