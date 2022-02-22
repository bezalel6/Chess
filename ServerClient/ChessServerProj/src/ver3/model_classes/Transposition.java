package ver3.model_classes;


import ver3.model_classes.eval_classes.Evaluation;

public class Transposition {
    private final int maxDepth;
    private final Player player;
    private Evaluation evaluation;

    public Transposition(Minimax.MinimaxParameters parms, Evaluation evaluation) {
        this.maxDepth = parms.getMaxDepth();
        this.player = parms.getMinimaxPlayer();
        this.evaluation = new Evaluation(evaluation);
    }

    public Player getPlayer() {
        return player;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
