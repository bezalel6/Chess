package ver14.Model.Hashing;


import ver14.Model.Minimax.MinimaxParameters;
import ver14.SharedClasses.Game.Evaluation.Evaluation;
import ver14.SharedClasses.Game.PlayerColor;

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
