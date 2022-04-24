package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Sync.SyncableItem;

import java.io.Serializable;
import java.util.Random;


public abstract class GameInfo implements Serializable, SyncableItem {

    public final String gameId;
    public final String creatorUsername;
    public final GameSettings gameSettings;

    protected GameInfo(String gameId, String creatorUsername, GameSettings gameSettings) {
        this.gameId = gameId;
        this.creatorUsername = creatorUsername;
        this.gameSettings = gameSettings;
    }

    public static GameInfo example() {
        return new CreatedGame(new Random().nextInt() + "", "creator", GameSettings.EXAMPLE);
    }

    public boolean isCreator(String username) {
        return creatorUsername.equals(username);
    }

    @Override
    public String ID() {
        return gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameInfo gameInfo = (GameInfo) o;
        return gameInfo.gameId.equals(gameId);
    }

    @Override
    public String toString() {
        return getGameDesc();
    }

    public abstract String getGameDesc();

    public PlayerColor getJoiningPlayerColor() {//no player's opp is itself
        return getStartingColor().getOpponent();
    }

    public PlayerColor getStartingColor() {
        return gameSettings.getPlayerToMove();
    }
}
