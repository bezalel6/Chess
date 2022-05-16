package ver14.SharedClasses.Game.Evaluation;

import java.io.Serializable;

/**
 * represents an Evaluation detail.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public record EvaluationDetail(EvaluationParameters parm, int eval) implements Serializable {
    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return parm + ": " + Evaluation.convertFromCentipawns(eval) + " * " + parm.weight;
    }
}
