package ver13.SharedClasses.SavedGames;

import ver13.SharedClasses.GameSettings;
import ver13.SharedClasses.moves.Move;

import java.util.Stack;

public class ArchivedGameInfo extends EstablishedGameInfo {
    private final String winner;

    public ArchivedGameInfo(String gameId, String creatorUsername, String opponentUsername, GameSettings gameSettings, String winner, Stack<Move> moveStack) {
        super(gameId, creatorUsername, opponentUsername, gameSettings, moveStack);
        this.winner = winner;
    }

    public String getWinner() {
        return winner;
    }
}
