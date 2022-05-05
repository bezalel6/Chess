package ver14.Game;

import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;

/**
 * Game over error - represents an error that will cause a game over. for example: a player disconnected.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
//    todo change to throwable
public class GameOverError extends MyError {

    /**
     * The Game over status.
     */
    public final GameStatus gameOverStatus;

    /**
     * Instantiates a new Game over error.
     *
     * @param gameOverStatus the game over status
     */
    public GameOverError(GameStatus gameOverStatus) {
        this.gameOverStatus = gameOverStatus;
    }
}
