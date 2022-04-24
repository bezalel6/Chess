package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;

import java.util.Date;
import java.util.Stack;


public abstract class EstablishedGameInfo extends GameInfo {
    public final String opponentUsername;
    private final Stack<Move> moveStack;
    protected Date createdAt;

    protected EstablishedGameInfo(String gameId, String creatorUsername, String opponentUsername, GameSettings gameSettings, Stack<Move> moveStack) {
        super(gameId, creatorUsername, gameSettings);
        this.opponentUsername = opponentUsername;
        this.moveStack = new Stack<>();
        moveStack.forEach(move -> this.moveStack.push(Move.copyMove(move)));
        createdAt = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Stack<Move> getMoveStack() {
        return moveStack;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getGameDesc() {
        return "%s Playing vs %s".formatted(creatorUsername, opponentUsername);
    }
}
