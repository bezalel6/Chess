package ver14.Model.Minimax;

import ver14.Model.Model;
import ver14.Players.PlayerAI.Stockfish.Stockfish;
import ver14.SharedClasses.Game.Evaluation.EvaluationParameters;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;
import ver14.SharedClasses.Game.PlayerColor;

public class MinimaxParametersAdjuster {

    Model model = new Model();
    Stockfish stockfish = new Stockfish();
    Minimax minimax = new Minimax(model, 5000);
    int maxDepth = 100;

    public static void main(String[] args) {
        MinimaxParametersAdjuster adjuster = new MinimaxParametersAdjuster();
        adjuster.start();
    }

    private void start() {
        try {
            run(Board.startingFen, 0, EvaluationParameters.MATERIAL);
        } finally {
            stockfish.stopEngine();
            minimax.end();
        }
    }

    private float run(String position, int depth, EvaluationParameters adjusting) {
        if (depth == maxDepth)
            return 999999;

        model.setup(position);

        double savedWeight = adjusting.weight;
        float tryingWeight = (float) (adjusting.weight + adjusting.adjustBy);
        adjusting.weight = tryingWeight;
        float stockfishEval = stockfish.getEvalScore(position, 10);
        float minimaxEval = minimax.getEvaluation(PlayerColor.WHITE).convertFromCentipawns();
        float diff = Math.abs(minimaxEval - stockfishEval);
        System.out.printf("weight = %s res = %s%n", tryingWeight, diff);

        float rec = run(position, depth + 1, adjusting);

        if (diff < rec) {
            adjusting.weight = tryingWeight;
            System.out.println("found better option. weight = " + adjusting.weight + " with a result of " + diff);
        } else {
            adjusting.weight = savedWeight;
        }

        return Math.min(diff, rec);


    }
}
