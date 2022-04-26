package ver14.SharedClasses.Game.Evaluation;


/**
 * Evaluation parameters - all evaluation parameters. (some are unused).
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum EvaluationParameters {
    /**
     * Material evaluation parameters.
     */
    MATERIAL(1.5),
    /**
     * Piece tables evaluation parameters.
     */
    PIECE_TABLES(1.1),
    /**
     * King safety evaluation parameters.
     */
    KING_SAFETY(.1),
    /**
     * Hanging pieces evaluation parameters.
     */
    HANGING_PIECES,
    /**
     * Square control evaluation parameters.
     */
    SQUARE_CONTROL,
    /**
     * Movement ability evaluation parameters.
     */
    MOVEMENT_ABILITY,
    /**
     * Force king to corner evaluation parameters.
     */
    FORCE_KING_TO_CORNER(1.5),
    /**
     * Eg weight evaluation parameters.
     */
    EG_WEIGHT,
    /**
     * Stockfish says evaluation parameters.
     */
    STOCKFISH_SAYS;
    /**
     * The parameter's weight
     */
    public final double weight;

    EvaluationParameters() {
        this(1);
    }

    EvaluationParameters(double weight) {
        this.weight = weight;
    }

}