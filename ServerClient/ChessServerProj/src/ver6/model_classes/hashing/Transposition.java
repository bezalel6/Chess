package ver6.model_classes.hashing;


import ver6.SharedClasses.Hashable;
import ver6.SharedClasses.PlayerColor;
import ver6.SharedClasses.evaluation.Evaluation;
import ver6.model_classes.minimax.Minimax;

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
