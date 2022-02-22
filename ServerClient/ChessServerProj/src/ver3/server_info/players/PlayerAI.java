package ver3.server_info.players;

import ver3.model_classes.moves.Move;

/**
 * PlayerAI.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public class PlayerAI extends Player {
    private int minimaxTimeoutInSec;

    public PlayerAI(int minimaxTimeout, String id) {
        super("AI");
        this.minimaxTimeoutInSec = minimaxTimeout;
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
    public Move getMove() {
        Move move = null;
        // do move with minimax
        // move = model.getBestMoveUsingMinimax(); 

        return move;
    }

    @Override
    public void waitTurn() {
    }

    @Override
    public void gameOver() {
    }
}
