package ver14.Model.minimax;

import ver14.Model.Model;
import ver14.Model.MoveGenerator.GenerationSettings;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.evaluation.Evaluation;
import ver14.SharedClasses.Game.moves.Move;

public class MinimaxParameters {
    public final Model model;
    public final boolean isMax;
    public final PlayerColor minimaxPlayerColor;
    public final int currentDepth, maxDepth;
    public @GenerationSettings
    int genSettings;
    public double a, b;

    public MinimaxParameters(Model model, boolean isMax, int maxDepth, PlayerColor minimaxPlayerColor, Move rootMove) {
        this(model, isMax, 1, maxDepth, minimaxPlayerColor, Evaluation.LOSS_EVAL, Evaluation.WIN_EVAL, GenerationSettings.LEGALIZE);
    }

    public MinimaxParameters(Model model, boolean isMax, int currentDepth, int maxDepth, PlayerColor minimaxPlayerColor, double a, double b, @GenerationSettings int genSettings) {
        this.model = model;
        this.genSettings = genSettings;
        this.isMax = isMax;
        this.maxDepth = maxDepth;
        this.minimaxPlayerColor = minimaxPlayerColor;
        this.currentDepth = currentDepth;
        this.a = a;
        this.b = b;

    }


    public boolean prune(Evaluation bestEval) {
        if (isMax) {
            a = Math.max(a, bestEval.getEval());
        } else {
            b = Math.min(b, bestEval.getEval());
        }
        return b <= a;
    }

//    private static final int


    public boolean isMax() {
        return isMax;
    }

    public PlayerColor getMinimaxPlayer() {
        return minimaxPlayerColor;
    }

    public int getCurrentDepth() {
        return currentDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public MinimaxParameters nextDepth() {
        return new MinimaxParameters(model, !isMax, currentDepth + 1, maxDepth, minimaxPlayerColor, a, b, genSettings);
    }
}
