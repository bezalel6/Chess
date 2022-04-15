package ver14.SharedClasses.Game.moves;

import ver14.SharedClasses.Game.evaluation.Evaluation;

import java.io.Serializable;
import java.util.Objects;

public class MinimaxMove implements Comparable<MinimaxMove>, Serializable {
    private Move move;
    private Evaluation moveEvaluation;
    private int moveDepth;

    public MinimaxMove(Move move, Evaluation moveEvaluation) {
        if (moveEvaluation != null)
            this.moveEvaluation = new Evaluation(moveEvaluation);
        this.move = Move.copyMove(move);
        this.moveDepth = moveEvaluation == null || moveEvaluation.getEvaluationDepth() == null ? -1 : moveEvaluation.getEvaluationDepth();
    }

    public MinimaxMove(Evaluation moveEvaluation) {
        this.moveEvaluation = moveEvaluation;
    }


    public MinimaxMove(MinimaxMove other) {
        move = Move.copyMove(other.move);
        moveEvaluation = new Evaluation(other.moveEvaluation);
        moveDepth = other.moveDepth;
    }


    public boolean isDeeperAndBetterThan(MinimaxMove other) {
        return other == null || (moveEvaluation != null && (moveEvaluation.isGreaterThan(other.moveEvaluation)) && moveDepth >= other.moveDepth);
    }

    public int getMoveDepth() {
        return moveDepth;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = Move.copyMove(move);
    }

    public Evaluation getMoveEvaluation() {
        return moveEvaluation;
    }

    public void setMoveEvaluation(Evaluation moveEvaluation) {
        this.moveEvaluation = moveEvaluation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(move, moveEvaluation, moveDepth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinimaxMove that = (MinimaxMove) o;
        return moveDepth == that.moveDepth && Objects.equals(move, that.move) && Objects.equals(moveEvaluation, that.moveEvaluation);
    }

    @Override
    public String toString() {
        return getShortPrintingStr();
    }

    public String getShortPrintingStr() {
        return "Move: " + move +
                " Evaluation: " + moveEvaluation +
                " Depth: " + moveDepth;
    }

    @Override
    public int compareTo(MinimaxMove o) {
        if (moveDepth == o.moveDepth) {
            return Double.compare(moveEvaluation.getEval(), o.moveEvaluation.getEval());
        }
        return Integer.compare(moveDepth, o.moveDepth);
    }
}
