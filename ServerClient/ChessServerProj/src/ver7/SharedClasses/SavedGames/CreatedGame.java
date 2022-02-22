package ver7.SharedClasses.SavedGames;

import ver7.SharedClasses.GameSettings;
import ver7.SharedClasses.PlayerColor;

public class CreatedGame extends GameInfo {

    public CreatedGame(String gameId, String creatorUsername, GameSettings gameSettings) {
        super(gameId, creatorUsername, gameSettings);
    }

    @Override
    public String getGameDesc() {
        return "%s %s (%s)".formatted(creatorUsername, getJoiningPlayerColor() == PlayerColor.NO_PLAYER ? "Random" : getJoiningPlayerColor(), gameId);

    }

}
