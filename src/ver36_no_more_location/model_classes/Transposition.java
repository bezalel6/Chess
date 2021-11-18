package ver36_no_more_location.model_classes;


import ver36_no_more_location.model_classes.eval_classes.Evaluation;

public class Transposition {
    private final int maxDepth, player;
    private Evaluation evaluation;

    public Transposition(Model.MinimaxParameters parms, Evaluation evaluation) {
        this.maxDepth = parms.getMaxDepth();
        this.player = parms.getMinimaxPlayer();
        this.evaluation = new Evaluation(evaluation);
    }

    public int getPlayer() {
        return player;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
