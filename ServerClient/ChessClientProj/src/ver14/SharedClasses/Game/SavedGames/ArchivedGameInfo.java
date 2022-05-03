package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;

import java.util.Stack;


/**
 * Archived game info.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ArchivedGameInfo extends EstablishedGameInfo {
    /**
     * The Winner.
     */
    private final String winner;

    /**
     * Instantiates a new Archived game info.
     *
     * @param gameId           the game id
     * @param creatorUsername  the creator username
     * @param opponentUsername the opponent username
     * @param gameSettings     the game settings
     * @param winner           the winner
     * @param moveStack        the move stack
     */
    public ArchivedGameInfo(String gameId, String creatorUsername, String opponentUsername, GameSettings gameSettings, String winner, Stack<Move> moveStack) {
        super(gameId, creatorUsername, opponentUsername, gameSettings, moveStack);
        this.winner = winner;
    }

    /**
     * Gets winner.
     *
     * @return the winner
     */
    public String getWinner() {
        return winner;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
