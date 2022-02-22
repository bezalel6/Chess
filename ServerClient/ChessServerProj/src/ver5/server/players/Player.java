package ver5.server.players;

import ver5.SharedClasses.PlayerColor;
import ver5.SharedClasses.evaluation.GameStatus;
import ver5.SharedClasses.moves.Move;
import ver5.server.game.Game;

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

    public boolean isGuest() {
        return false;
    }

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


    public abstract void disconnect(String cause);

    public abstract void waitForMatch();

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

}
