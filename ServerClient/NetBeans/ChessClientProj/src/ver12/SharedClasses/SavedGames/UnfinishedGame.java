package ver12.SharedClasses.SavedGames;

import ver12.SharedClasses.GameSettings;
import ver12.SharedClasses.PlayerColor;
import ver12.SharedClasses.moves.Move;

import java.util.Stack;

public class UnfinishedGame extends EstablishedGameInfo {
    public final PlayerColor playerColorToMove;
    public final String playerToMove;


    public UnfinishedGame(String gameId,
                          String creatorUsername,
                          GameSettings gameSettings,
                          String opponentUsername,
                          PlayerColor playerColorToMove,
                          String playerToMove,
                          Stack<Move> moveStack) {
        super(gameId, creatorUsername, opponentUsername, gameSettings, moveStack);
        this.playerColorToMove = playerColorToMove;
        this.playerToMove = playerToMove;
    }

    public boolean isCreatorToMove() {
        return isCreator(playerToMove);
    }
}