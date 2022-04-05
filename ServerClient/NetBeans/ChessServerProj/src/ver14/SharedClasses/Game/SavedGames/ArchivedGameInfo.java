package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.moves.Move;

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
