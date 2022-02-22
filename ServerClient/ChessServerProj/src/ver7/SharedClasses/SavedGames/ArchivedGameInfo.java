package ver7.SharedClasses.SavedGames;

import ver7.SharedClasses.GameSettings;
import ver7.SharedClasses.moves.Move;

import java.util.Stack;

public class ArchivedGameInfo extends EstablishedGameInfo {
    public final String winner;


    public ArchivedGameInfo(String gameId, String creatorUsername, String opponentUsername, GameSettings gameSettings, String winner, Stack<Move> moveStack) {
        super(gameId, creatorUsername, opponentUsername, gameSettings, moveStack);
        this.winner = winner;
    }
}
