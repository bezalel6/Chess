package ver14.SharedClasses.Game.Moves;

import ver14.SharedClasses.Game.Evaluation.Evaluation;

import java.io.Serializable;
import java.util.Objects;


/**
 * represents a Minimax move with a score and depth.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MinimaxMove implements Comparable<MinimaxMove>, Serializable {
    /**
     * The Move.
     */
    private Move move;
    /**
     * The Move evaluation.
     */
    private Evaluation moveEvaluation;
    /**
     * The Move depth.
     */
    private int moveDepth;

    /**
     * Instantiates a new Minimax move.
     *
     * @param move           the move
     * @param moveEvaluation the move evaluation
     */
    public MinimaxMove(Move move, Evaluation moveEvaluation) {
        if (moveEvaluation != null)
            this.moveEvaluation = new Evaluation(moveEvaluation);
        this.move = Move.copyMove(move);
        this.moveDepth = moveEvaluation == null || moveEvaluation.getEvaluationDepth() == null ? -1 : moveEvaluation.getEvaluationDepth();
    }

    /**
     * Instantiates a new Minimax move.
     *
     * @param moveEvaluation the move evaluation
     */
    public MinimaxMove(Evaluation moveEvaluation) {
        this.moveEvaluation = moveEvaluation;
    }


    /**
     * Instantiates a new Minimax move.
     *
     * @param other the other
     */
    public MinimaxMove(MinimaxMove other) {
        move = Move.copyMove(other.move);
        moveEvaluation = new Evaluation(other.moveEvaluation);
        moveDepth = other.moveDepth;
    }


    /**
     * Is deeper and better than given minimax move.
     *
     * @param other the other
     * @return the boolean
     */
    public boolean isDeeperAndBetterThan(MinimaxMove other) {
        return other == null || (moveEvaluation != null && (moveEvaluation.isGreaterThan(other.moveEvaluation)) && moveDepth >= other.moveDepth);
    }

    /**
     * Gets move depth.
     *
     * @return the move depth
     */
    public int getMoveDepth() {
        return moveDepth;
    }

    /**
     * Gets move.
     *
     * @return the move
     */
    public Move getMove() {
        return move;
    }

    /**
     * Sets move.
     *
     * @param move the move
     */
    public void setMove(Move move) {
        this.move = Move.copyMove(move);
    }

    /**
     * Gets move evaluation.
     *
     * @return the move evaluation
     */
    public Evaluation getMoveEvaluation() {
        return moveEvaluation;
    }

    /**
     * Sets move evaluation.
     *
     * @param moveEvaluation the move evaluation
     */
    public void setMoveEvaluation(Evaluation moveEvaluation) {
        this.moveEvaluation = moveEvaluation;
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(move, moveEvaluation, moveDepth);
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinimaxMove that = (MinimaxMove) o;
        return moveDepth == that.moveDepth && Objects.equals(move, that.move) && Objects.equals(moveEvaluation, that.moveEvaluation);
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return getShortPrintingStr();
    }

    /**
     * Gets short printing str.
     *
     * @return the short printing str
     */
    public String getShortPrintingStr() {
        return "Move: " + move +
                " Evaluation: " + moveEvaluation +
                " Depth: " + moveDepth;
    }

    /**
     * Compare to int.
     *
     * @param o the o
     * @return the int
     */
    @Override
    public int compareTo(MinimaxMove o) {
        if (moveDepth == o.moveDepth) {
            return Double.compare(moveEvaluation.getEval(), o.moveEvaluation.getEval());
        }
        return Integer.compare(moveDepth, o.moveDepth);
    }
}
