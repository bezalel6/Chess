package ver12.Model.hashing;


import ver12.Model.minimax.Minimax;
import ver12.SharedClasses.PlayerColor;
import ver12.SharedClasses.evaluation.Evaluation;

public class Transposition {
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
