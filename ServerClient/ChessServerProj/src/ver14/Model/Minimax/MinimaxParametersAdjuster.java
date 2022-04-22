package ver14.Model.Minimax;

import ver14.Model.Model;
import ver14.Players.PlayerAI.Stockfish.Stockfish;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;
import ver14.SharedClasses.Game.PlayerColor;

public class MinimaxParametersAdjuster {

    public static void main(String[] args) {
        MinimaxParametersAdjuster adjuster = new MinimaxParametersAdjuster();
        adjuster.adjust(Board.startingFen);
    }

    private void adjust(String position) {
        Model model = new Model(position);
        Stockfish stockfish = new Stockfish();
        Minimax minimax = new Minimax(model, 1000);
        try {

            float stockfishEval = stockfish.getEvalScore(position, 10);
            float minimaxEval = minimax.getEvaluation(PlayerColor.WHITE).getAdjusted();

            System.out.println("stockfish = " + stockfishEval);
            System.out.println("minimax = " + minimaxEval);
        } finally {
            stockfish.stopEngine();
            minimax.end();
        }

    }
}
