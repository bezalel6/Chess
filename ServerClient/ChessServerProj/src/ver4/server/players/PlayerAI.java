package ver4.server.players;

import ver4.SharedClasses.evaluation.GameStatus;
import ver4.SharedClasses.moves.Move;
import ver4.model_classes.Minimax;
import ver4.model_classes.Model;

/**
 * PlayerAI.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public class PlayerAI extends Player {
    private int minimaxTimeoutInSec;
    private Minimax minimax = null;

    public PlayerAI(int minimaxTimeout) {
        super("AI");
        this.minimaxTimeoutInSec = minimaxTimeout;
    }

    @Override
    public void setModel(Model model) {
        super.setModel(model);
        minimax = new Minimax(model, minimaxTimeoutInSec);
    }

    public int getMinimaxTimeout() {
        return minimaxTimeoutInSec;
    }

    public void setMinimaxTimeout(int minimaxTimeout) {
        this.minimaxTimeoutInSec = minimaxTimeout;
    }

    @Override
    public String toString() {
        return "PlayerAI{" + super.toString() + ", minimaxTimeout=" + minimaxTimeoutInSec + '}';
    }

    @Override
    public void disconnect(String cause) {
        
    }

    @Override
    public Move getMove() {
        assert minimax != null;
        return minimax.getBestMove();
    }

    @Override
    public void waitTurn() {
    }

    @Override
    public void gameOver(GameStatus gameStatus) {
    }

    @Override
    public boolean askForRematch() {
        return true;
    }
}
