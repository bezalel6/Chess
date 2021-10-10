package ver29_minimax_threading.model_classes.moves;

import ver29_minimax_threading.model_classes.Board;
import ver29_minimax_threading.model_classes.eval_classes.Evaluation;

import java.util.Objects;

public class MinimaxMove implements Comparable<MinimaxMove> {
    private Move move;
    private Evaluation moveEvaluation;
    private int moveDepth;
    private boolean completeSearch = true;
    private boolean isMax;
    private Board board;

    public MinimaxMove(Move move, Evaluation moveEvaluation, int moveDepth) {
        this(move, moveEvaluation, moveDepth, null, true);
    }

    public MinimaxMove(Move move, Evaluation moveEvaluation, int moveDepth, Board board, boolean isMax) {
        this.move = Move.copyMove(move);
        this.moveDepth = moveDepth;
        this.moveEvaluation = new Evaluation(moveEvaluation);
        this.board = board;
        this.isMax = isMax;
    }

    public MinimaxMove(Evaluation moveEvaluation) {
        this.moveEvaluation = moveEvaluation;
    }

    public MinimaxMove(MinimaxMove other, Board board) {
        this(other);
        this.board = board;
    }


    public MinimaxMove(MinimaxMove other) {
        move = Move.copyMove(other.move);
        moveEvaluation = new Evaluation(other.moveEvaluation);
        moveDepth = other.moveDepth;
        completeSearch = other.completeSearch;
        this.board = other.board;
        this.isMax = other.isMax;
    }

    public boolean isMax() {
        return isMax;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public boolean isBetterThan(MinimaxMove other) {
        return other == null || moveEvaluation.isGreaterThan(other.moveEvaluation);
    }

    public boolean isDeeperAndBetterThan(MinimaxMove other) {
        return isBetterThan(other) && isDeeper(other);
    }

    public boolean isDeeper(MinimaxMove other) {
        return other == null || moveDepth >= other.moveDepth;
    }

    public boolean isCompleteSearch() {
        return completeSearch;
    }

    public void setCompleteSearch(boolean completeSearch) {
        this.completeSearch = completeSearch;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinimaxMove that = (MinimaxMove) o;
        return moveDepth == that.moveDepth && completeSearch == that.completeSearch && Objects.equals(move, that.move) && Objects.equals(moveEvaluation, that.moveEvaluation) && Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(move, moveEvaluation, moveDepth, completeSearch, board);
    }

    @Override
    public String toString() {
        return getShortPrintingStr();
//        return "MinimaxMove{" +
//                "move=" + move +
//                ", moveValue=" + moveEvaluation +
//                ", moveDepth=" + moveDepth +
//                ", completeSearch=" + completeSearch +
//                '}';
    }

    public String getShortPrintingStr() {
        return "Move: " + move +
                "Evaluation: " + moveEvaluation +
                "Depth: " + moveDepth;
    }

    @Override
    public int compareTo(MinimaxMove o) {
        if (moveDepth == o.moveDepth) {
            return Double.compare(moveEvaluation.getEval(), o.moveEvaluation.getEval());
        }
        return Integer.compare(moveDepth, o.moveDepth);
    }
}
