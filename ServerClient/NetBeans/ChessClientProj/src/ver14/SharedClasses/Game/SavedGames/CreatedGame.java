package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.PlayerColor;


/**
 * Created game.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class CreatedGame extends GameInfo {

    /**
     * Instantiates a new Created game.
     *
     * @param gameId          the game id
     * @param creatorUsername the creator username
     * @param gameSettings    the game settings
     */
    public CreatedGame(String gameId, String creatorUsername, GameSettings gameSettings) {
        super(gameId, creatorUsername, gameSettings);
    }

    /**
     * Gets game desc.
     *
     * @return the game desc
     */
    @Override
    public String getGameDesc() {
        return "%s %s (%s)".formatted(creatorUsername, getJoiningPlayerColor() == PlayerColor.NO_PLAYER ? "Random" : getJoiningPlayerColor(), gameId);

    }

}
