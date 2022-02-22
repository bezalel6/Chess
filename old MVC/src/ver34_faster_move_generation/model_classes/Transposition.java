package ver34_faster_move_generation.model_classes;


import ver34_faster_move_generation.model_classes.eval_classes.Evaluation;

public class Transposition {
    private final int maxDepth, player;
    private Evaluation evaluation;

    public Transposition(int maxDepth, int player, Evaluation evaluation) {
        this.maxDepth = maxDepth;
        this.player = player;
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
