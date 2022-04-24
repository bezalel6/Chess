package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.PlayerColor;


public class CreatedGame extends GameInfo {

    public CreatedGame(String gameId, String creatorUsername, GameSettings gameSettings) {
        super(gameId, creatorUsername, gameSettings);
    }

    @Override
    public String getGameDesc() {
        return "%s %s (%s)".formatted(creatorUsername, getJoiningPlayerColor() == PlayerColor.NO_PLAYER ? "Random" : getJoiningPlayerColor(), gameId);

    }

}
