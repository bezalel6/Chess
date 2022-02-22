package ver4.server.players;

import ver4.SharedClasses.PlayerColor;
import ver4.SharedClasses.evaluation.GameStatus;
import ver4.SharedClasses.moves.Move;
import ver4.model_classes.Model;

/**
 * Player.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public abstract class Player {
    Model model;
    PlayerColor playerColor;
    private Player partner = null;
    private String id;

    public Player(String id) {
        this.id = id;
    }

    public Player getPartner() {
        return partner;
    }

    public void setPartner(Player partner) {
        this.partner = partner;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract Move getMove();

    public abstract void waitTurn();

    public abstract void gameOver(GameStatus gameStatus);

    public abstract boolean askForRematch();

    @Override
    public String toString() {
        if (playerColor != null) {
            return id + " Playing as " + playerColor;
        }
        return id;
    }

    public abstract void disconnect(String cause);
}
