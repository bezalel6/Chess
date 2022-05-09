package ver14.SharedClasses.Game.SavedGames;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Sync.SyncableItem;

import java.io.Serial;
import java.io.Serializable;


/**
 * Game info.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class GameInfo implements Serializable, SyncableItem {
    @Serial
    private static final long serialVersionUID = 42069_000_000L;
    /**
     * The Game id.
     */
    public final String gameId;
    /**
     * The Creator username.
     */
    public final String creatorUsername;
    /**
     * The Game settings.
     */
    public final GameSettings gameSettings;

    /**
     * Instantiates a new Game info.
     *
     * @param gameId          the game id
     * @param creatorUsername the creator username
     * @param gameSettings    the game settings
     */
    protected GameInfo(String gameId, String creatorUsername, GameSettings gameSettings) {
        this.gameId = gameId;
        this.creatorUsername = creatorUsername;
        this.gameSettings = gameSettings;
    }

    /**
     * Is creator boolean.
     *
     * @param username the username
     * @return the boolean
     */
    public boolean isCreator(String username) {
        return creatorUsername.equals(username);
    }

    /**
     * Id string.
     *
     * @return the string
     */
    @Override
    public String ID() {
        return gameId;
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameInfo gameInfo = (GameInfo) o;
        return gameInfo.gameId.equals(gameId);
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return getGameDesc();
    }

    /**
     * Gets game desc.
     *
     * @return the game desc
     */
    public abstract String getGameDesc();

    /**
     * Gets joining player color.
     *
     * @return the joining player color
     */
    public PlayerColor getJoiningPlayerColor() {//no player's opp is itself
        return getStartingColor().getOpponent();
    }

    /**
     * Gets starting color.
     *
     * @return the starting color
     */
    public PlayerColor getStartingColor() {
        return gameSettings.getPlayerToMove();
    }
}
