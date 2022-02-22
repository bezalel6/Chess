package ver11.Model.hashing;


import ver11.Model.minimax.Minimax;
import ver11.SharedClasses.Hashable;
import ver11.SharedClasses.PlayerColor;
import ver11.SharedClasses.evaluation.Evaluation;

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
