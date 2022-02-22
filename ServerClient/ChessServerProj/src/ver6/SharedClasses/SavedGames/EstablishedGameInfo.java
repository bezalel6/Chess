package ver6.SharedClasses.SavedGames;

import ver6.SharedClasses.GameSettings;
import ver6.SharedClasses.moves.Move;

import java.util.Stack;

public abstract class EstablishedGameInfo extends GameInfo {
    public final String opponentUsername;
    private final Stack<Move> moveStack;

    protected EstablishedGameInfo(String gameId, String creatorUsername, String opponentUsername, GameSettings gameSettings, Stack<Move> moveStack) {
        super(gameId, creatorUsername, gameSettings);
        this.opponentUsername = opponentUsername;
        this.moveStack = new Stack<>();
        moveStack.forEach(move -> this.moveStack.push(Move.copyMove(move)));
    }

    public Stack<Move> getMoveStack() {
        return moveStack;
    }
}
