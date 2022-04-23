package ver14.Model.Minimax;

import ver14.Model.Eval.Eval;
import ver14.Model.Model;
import ver14.Model.ModelMovesList;
import ver14.SharedClasses.Game.Evaluation.Evaluation;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;

/*
 * NewMinimax
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * NewMinimax -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * NewMinimax -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class NewMinimax {
    private final Model model;
    private final PlayerColor playerColor;

    public NewMinimax(Model model, PlayerColor playerColor) {
        this.model = model;
        this.playerColor = playerColor;
    }

    public Evaluation minimax(int depth, int maxDepth, boolean isMax, double a, double b) {
        if (depth >= maxDepth || Eval.isGameOver(model)) {
            return Eval.getEvaluation(model, playerColor);
        }
        Evaluation bestEval = null;
        ModelMovesList moves = model.generateAllMoves();
        for (Move move : moves) {
            model.applyMove(move);

            Evaluation eval = minimax(depth + 1, maxDepth, !isMax, a, b);

            model.undoMove(move);

            if (bestEval == null || eval.isGreaterThan(bestEval) == isMax) {
                bestEval = eval;
            }
            if (isMax) {
                a = Math.max(a, bestEval.getEval());
            } else {
                b = (Math.max(b, bestEval.getEval()));
            }
            if (b <= a)
                break;


        }
        return bestEval;
    }

}
