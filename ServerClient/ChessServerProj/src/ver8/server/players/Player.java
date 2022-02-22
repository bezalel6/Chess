package ver8.server.players;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.PlayerColor;
import ver8.SharedClasses.Sync.SyncedItems;
import ver8.SharedClasses.evaluation.GameStatus;
import ver8.SharedClasses.moves.Move;
import ver8.server.game.Game;

/**
 * Player.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public abstract class Player {
    Game game;
    PlayerColor playerColor;
    private Player partner = null;
    private String username;

    public Player(String id) {
        this.username = id;
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
}
