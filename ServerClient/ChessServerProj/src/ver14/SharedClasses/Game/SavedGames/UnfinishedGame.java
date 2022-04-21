package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;

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
