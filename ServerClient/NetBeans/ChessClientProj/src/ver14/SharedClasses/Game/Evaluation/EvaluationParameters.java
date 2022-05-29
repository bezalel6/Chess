package ver14.SharedClasses.Game.Evaluation;


/**
 * represents an evaluation parameter. a metric on which a position might get evaluated by.
 * an evaluation parameter has a weight, that decides how much influence it has on the final evaluation of a position.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum EvaluationParameters {
    /**
     * pieces values evaluation parameter.
     */
    MATERIAL(1.5),
    /**
     * piece tables evaluation parameter.
     */
    PIECE_TABLES(1.3),
//    /**
//     * King safety evaluation parameter.
//     */
//    KING_SAFETY(.1),
//    /**
//     * Hanging pieces evaluation parameter.
//     */
//    HANGING_PIECES,
//    /**
//     * Square control evaluation parameter.
//     */
//    SQUARE_CONTROL,
//    /**
//     * Movement ability evaluation parameter.
//     */
//    MOVEMENT_ABILITY,
    /**
     * Force king to corner evaluation parameter.
     */
    FORCE_KING_TO_CORNER(.8);
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