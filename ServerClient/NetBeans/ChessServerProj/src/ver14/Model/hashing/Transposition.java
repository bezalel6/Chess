package ver14.Model.hashing;


import ver14.Model.minimax.MinimaxParameters;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.evaluation.Evaluation;

public class Transposition {
    private final int maxDepth;
    private final PlayerColor playerColor;
    private Evaluation evaluation;

    public Transposition(MinimaxParameters parms, Evaluation evaluation) {
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