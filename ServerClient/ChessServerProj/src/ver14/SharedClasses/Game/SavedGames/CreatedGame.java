package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.PlayerColor;

/*
 * CreatedGame
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * CreatedGame -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * CreatedGame -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class CreatedGame extends GameInfo {

    public CreatedGame(String gameId, String creatorUsername, GameSettings gameSettings) {
        super(gameId, creatorUsername, gameSettings);
    }

    @Override
    public String getGameDesc() {
        return "%s %s (%s)".formatted(creatorUsername, getJoiningPlayerColor() == PlayerColor.NO_PLAYER ? "Random" : getJoiningPlayerColor(), gameId);

    }

}
