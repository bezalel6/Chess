package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;

import java.util.Stack;

/*
 * UnfinishedGame
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * UnfinishedGame -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * UnfinishedGame -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

/**
 * The type Unfinished game.
 */
public class UnfinishedGame extends EstablishedGameInfo {
    /**
     * The Player color to move.
     */
    public final PlayerColor playerColorToMove;
    /**
     * The Player to move.
     */
    public final String playerToMove;


    /**
     * Instantiates a new Unfinished game.
     *
     * @param gameId            the game id
     * @param creatorUsername   the creator username
     * @param gameSettings      the game settings
     * @param opponentUsername  the opponent username
     * @param playerColorToMove the player color to move
     * @param playerToMove      the player to move
     * @param moveStack         the move stack
     */
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

    /**
     * Is creator to move boolean.
     *
     * @return the boolean
     */
    public boolean isCreatorToMove() {
        return isCreator(playerToMove);
    }
}
