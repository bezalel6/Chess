package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.moves.Move;

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

    @Override
    public String getGameDesc() {
        return "%s Playing vs %s".formatted(creatorUsername, opponentUsername);
    }

    public Stack<Move> getMoveStack() {
        return moveStack;
    }
}