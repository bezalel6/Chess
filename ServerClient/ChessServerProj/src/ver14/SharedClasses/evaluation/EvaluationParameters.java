package ver14.SharedClasses.evaluation;

import java.io.Serializable;

public class EvaluationParameters implements Serializable {
    public static final int NUM_OF_EVAL_PARAMETERS = 9;
    public static final int MATERIAL = 0;
    public static final int PIECE_TABLES = 1;
    public static final int KING_SAFETY = 2;
    public static final int HANGING_PIECES = 3;
    public static final int SQUARE_CONTROL = 4;
    public static final int MOVEMENT_ABILITY = 5;
    public static final int FORCE_KING_TO_CORNER = 6;
    public static final int EG_WEIGHT = 7;
    public static final int STOCKFISH_SAYS = 8;
    public static final String[] EVAL_PARAMETERS_NAMES = initEvalParametersArr();

    private static String[] initEvalParametersArr() {
        String[] ret = new String[EvaluationParameters.NUM_OF_EVAL_PARAMETERS];
        ret[EvaluationParameters.MATERIAL] = "Material";
        ret[EvaluationParameters.PIECE_TABLES] = "Piece Tables";
        ret[EvaluationParameters.KING_SAFETY] = "King Safety";
        ret[EvaluationParameters.HANGING_PIECES] = "Hanging Pieces";
        ret[EvaluationParameters.SQUARE_CONTROL] = "Square Control";
        ret[EvaluationParameters.MOVEMENT_ABILITY] = "Movement Ability";
        ret[EvaluationParameters.FORCE_KING_TO_CORNER] = "Force King To Corner Endgame Eval";
        ret[EvaluationParameters.EG_WEIGHT] = "Endgame Weight";
        ret[EvaluationParameters.STOCKFISH_SAYS] = "Stockfish Says";
        return ret;
    }
}