package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;

import java.util.Date;
import java.util.Stack;


/**
 * Established game info.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class EstablishedGameInfo extends GameInfo {
    /**
     * The Opponent username.
     */
    public final String opponentUsername;
    /**
     * The Move stack.
     */
    private final Stack<Move> moveStack;
    /**
     * The Created at.
     */
    protected Date createdAt;

    /**
     * Instantiates a new Established game info.
     *
     * @param gameId           the game id
     * @param creatorUsername  the creator username
     * @param opponentUsername the opponent username
     * @param gameSettings     the game settings
     * @param moveStack        the move stack
     */
    protected EstablishedGameInfo(String gameId, String creatorUsername, String opponentUsername, GameSettings gameSettings, Stack<Move> moveStack) {
        super(gameId, creatorUsername, gameSettings);
        this.opponentUsername = opponentUsername;
        this.moveStack = new Stack<>();
        moveStack.forEach(move -> this.moveStack.push(Move.copyMove(move)));
        createdAt = new Date();
    }

    /**
     * Gets created at.
     *
     * @return the created at
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets created at.
     *
     * @param createdAt the created at
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets move stack.
     *
     * @return the move stack
     */
    public Stack<Move> getMoveStack() {
        return moveStack;
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

    /**
     * Gets game desc.
     *
     * @return the game desc
     */
    @Override
    public String getGameDesc() {
        return "%s Playing vs %s".formatted(creatorUsername, opponentUsername);
    }
}
