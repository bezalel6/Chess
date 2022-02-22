package ver9.server.players;

import ver9.SharedClasses.GameSettings;
import ver9.SharedClasses.PlayerColor;
import ver9.SharedClasses.Sync.SyncedItems;
import ver9.SharedClasses.evaluation.GameStatus;
import ver9.SharedClasses.moves.Move;
import ver9.server.game.Game;
import ver9.server.game.GameSession;

/**
 * Player.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public abstract class Player {
    protected Game game;
    protected GameSession gameSession;
    PlayerColor playerColor;
    private Player partner = null;
    private String username;

    public Player(String id) {
        this.username = id;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public Player getPartner() {
        return partner;
    }

    public void setPartner(Player partner) {
        this.partner = partner;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void initGame(Game game) {
        this.game = game;
        initGame();
    }

    protected abstract void initGame();

    public void playerDisconnected() {
        System.out.println(this + " disconnected");
        if (game != null) {
            game.playerDisconnected(this);
        }
    }

    public abstract Move getMove();

    public abstract void waitTurn();

    public abstract void gameOver(GameStatus gameStatus);

    public abstract boolean askForRematch();

    public abstract void updateByMove(Move move);

    public abstract void cancelRematch();

    public abstract void interrupt();

    public abstract void disconnect(String cause);

    public abstract void waitForMatch();

    public abstract GameSettings getGameSettings(SyncedItems joinableGames, SyncedItems resumableGames);

    public Game getOnGoingGame() {
        return game;
    }

    @Override
    public String toString() {
        if (playerColor != null) {
            return username + " " + playerColor;
        }
        return username;
    }

    public boolean isSaveWorthy() {
        return !isGuest() && !isAi();
    }

    public abstract boolean isGuest();

    public abstract boolean isAi();

    public boolean isConnected() {
        return true;
    }
}
