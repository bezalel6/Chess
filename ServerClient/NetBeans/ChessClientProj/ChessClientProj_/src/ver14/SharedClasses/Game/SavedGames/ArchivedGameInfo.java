package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.DBActions.RequestBuilder;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;

import java.util.Stack;


/**
 * represents a game that was finished, and is intended to be archived in the db.
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
     * Gets the winner of this game.
     *
     * @return the winner's username if the game is decisive. {@value RequestBuilder#TIE_STR} otherwise
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
