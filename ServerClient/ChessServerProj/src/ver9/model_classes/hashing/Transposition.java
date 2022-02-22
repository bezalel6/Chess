package ver9.model_classes.hashing;


import ver9.SharedClasses.Hashable;
import ver9.SharedClasses.PlayerColor;
import ver9.SharedClasses.evaluation.Evaluation;
import ver9.model_classes.minimax.Minimax;

public class Transposition implements Hashable {
    private final int maxDepth;
    private final PlayerColor playerColor;
    private Evaluation evaluation;

    public Transposition(Minimax.MinimaxParameters parms, Evaluation evaluation) {
        this.maxDepth = parms.getMaxDepth();
        this.playerColor = parms.getMinimaxPlayer();
        this.evaluation = new Evaluation(evaluation);
    }

    public PlayerColor getPlayer() {
        return playerColor;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

}
