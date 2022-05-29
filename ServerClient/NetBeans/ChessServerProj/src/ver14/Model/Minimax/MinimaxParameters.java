package ver14.Model.Minimax;

import ver14.Model.Model;
import ver14.SharedClasses.Game.Evaluation.Evaluation;
import ver14.SharedClasses.Game.PlayerColor;


/**
 * parameters used by the Minimax.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MinimaxParameters {
    /**
     * The Model.
     */
    public final Model model;
    /**
     * The Is max.
     */
    public final boolean isMax;
    /**
     * The Minimax player color.
     */
    public final PlayerColor minimaxPlayerColor;
    /**
     * The Current depth.
     */
    public final int currentDepth, /**
     * The Max depth.
     */
    maxDepth;
    /**
     * The A.
     */
    public int a, /**
     * The B.
     */
    b;

    /**
     * Instantiates a new Minimax parameters.
     *
     * @param model              the model
     * @param isMax              the is max
     * @param maxDepth           the max depth
     * @param minimaxPlayerColor the minimax player color
     */
    public MinimaxParameters(Model model, boolean isMax, int maxDepth, PlayerColor minimaxPlayerColor) {
        this(model, isMax, 1, maxDepth, minimaxPlayerColor, Evaluation.LOSS_EVAL, Evaluation.WIN_EVAL);
    }

    /**
     * Instantiates a new Minimax parameters.
     *
     * @param model              the model
     * @param isMax              the is max
     * @param currentDepth       the current depth
     * @param maxDepth           the max depth
     * @param minimaxPlayerColor the minimax player color
     * @param a                  the a
     * @param b                  the b
     */
    public MinimaxParameters(Model model, boolean isMax, int currentDepth, int maxDepth, PlayerColor minimaxPlayerColor, int a, int b) {
        this.model = model;
        this.isMax = isMax;
        this.maxDepth = maxDepth;
        this.minimaxPlayerColor = minimaxPlayerColor;
        this.currentDepth = currentDepth;
        this.a = a;
        this.b = b;

    }


    /**
     * Prune boolean.
     *
     * @param bestEval the best eval
     * @return the boolean
     */
    public boolean prune(Evaluation bestEval) {
        if (isMax) {
            a = Math.max(a, bestEval.getEval());
        } else {
            b = Math.min(b, bestEval.getEval());
        }
        return b <= a;
    }


    /**
     * Is max boolean.
     *
     * @return the boolean
     */
    public boolean isMax() {
        return isMax;
    }

    /**
     * Gets minimax player.
     *
     * @return the minimax player
     */
    public PlayerColor getMinimaxPlayer() {
        return minimaxPlayerColor;
    }

    /**
     * Gets current depth.
     *
     * @return the current depth
     */
    public int getCurrentDepth() {
        return currentDepth;
    }

    /**
     * Gets max depth.
     *
     * @return the max depth
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Gets a.
     *
     * @return the a
     */
    public double getA() {
        return a;
    }

    /**
     * Gets b.
     *
     * @return the b
     */
    public double getB() {
        return b;
    }

    /**
     * Next depth minimax parameters.
     *
     * @return the minimax parameters
     */
    public MinimaxParameters nextDepth() {
        return new MinimaxParameters(model, !isMax, currentDepth + 1, maxDepth, minimaxPlayerColor, a, b);
    }
}
