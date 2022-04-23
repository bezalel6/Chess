package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;

import java.util.Stack;

/*
 * ArchivedGameInfo
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * ArchivedGameInfo -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * ArchivedGameInfo -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class ArchivedGameInfo extends EstablishedGameInfo {
    private final String winner;

    public ArchivedGameInfo(String gameId, String creatorUsername, String opponentUsername, GameSettings gameSettings, String winner, Stack<Move> moveStack) {
        super(gameId, creatorUsername, opponentUsername, gameSettings, moveStack);
        this.winner = winner;
    }

    public String getWinner() {
        return winner;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
