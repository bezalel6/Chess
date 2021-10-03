package ver26_save_minimax_levels.moves;

import ver26_save_minimax_levels.model_classes.Board;
import ver26_save_minimax_levels.model_classes.eval_classes.Evaluation;

import java.util.Objects;

public class MinimaxMove implements Comparable {
    private Move move;
    private Evaluation moveEvaluation;
    private int moveDepth;
    private int moveIndex = -1;
    private boolean completeSearch = true;
    private Board board;
    private double[] alphaBeta;

    public MinimaxMove(Move move, Evaluation moveEvaluation, int moveDepth, int moveIndex, Board board, double[] alphaBeta) {
        this(move, moveEvaluation, moveDepth, moveIndex, board);
        this.alphaBeta = alphaBeta.clone();
    }

    public MinimaxMove(Move move, Evaluation moveEvaluation, int moveDepth, int moveIndex, Board board) {
        this(move, moveEvaluation, moveDepth, board);
        this.moveIndex = moveIndex;
    }

    public MinimaxMove(Move move, Evaluation moveEvaluation, int moveDepth, int moveIndex) {
        this(move, moveEvaluation, moveDepth, moveIndex, null);
    }

    public MinimaxMove(Move move, Evaluation moveEvaluation, int moveDepth, Board board) {
        this.move = Move.copyMove(move);
        this.moveDepth = moveDepth;
        this.moveEvaluation = new Evaluation(moveEvaluation);
        this.board = board;
//        this.board = board == null ? null : new Board(board);
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
        moveIndex = other.moveIndex;
        this.board = other.board;
        if (other.alphaBeta != null)
            this.alphaBeta = other.alphaBeta.clone();
    }

    public double[] getAlphaBeta() {
        return alphaBeta;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public boolean isBetterThan(MinimaxMove other) {
        return other == null || moveEvaluation.isGreaterThan(other.moveEvaluation);
//        return other == null || moveValue.isGreaterThan(other.moveValue) || (moveValue.equals(other.moveValue) && moveDepth < other.moveDepth);
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
    public int compareTo(Object o) {
        MinimaxMove other = (MinimaxMove) o;
        return Double.compare(this.moveEvaluation.getEval(), other.moveEvaluation.getEval());
        //        return (int) (this.moveValue.getEval() - other.moveValue.getEval());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinimaxMove that = (MinimaxMove) o;
        return moveDepth == that.moveDepth && moveIndex == that.moveIndex && completeSearch == that.completeSearch && Objects.equals(move, that.move) && Objects.equals(moveEvaluation, that.moveEvaluation) && Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(move, moveEvaluation, moveDepth, moveIndex, completeSearch, board);
    }

    @Override
    public String toString() {
        return "MinimaxMove{" +
                "move=" + move +
                ", moveValue=" + moveEvaluation +
                ", moveDepth=" + moveDepth +
                ", completeSearch=" + completeSearch +
                (moveIndex == -1 ? "" : ", moveIndex= " + moveIndex) +
                '}';
    }

    public int getMoveIndex() {
        return moveIndex;
    }

    public void setMoveIndex(int moveIndex) {
        this.moveIndex = moveIndex;
    }
}
