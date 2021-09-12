package ver22_eval_captures.moves;

import ver22_eval_captures.model_classes.eval_classes.Evaluation;

public class MinimaxMove {
    private Move move;
    private Evaluation moveValue;
    private int moveDepth;

    public MinimaxMove(Move move, Evaluation moveValue, int moveDepth) {
        this.move = Move.copyMove(move);
        this.moveDepth = moveDepth;
        this.moveValue = new Evaluation(moveValue);
    }


    public MinimaxMove(Evaluation moveValue) {
        this.moveValue = moveValue;
    }

    public MinimaxMove(MinimaxMove other) {
        move = Move.copyMove(other.move);
        moveValue = new Evaluation(other.moveValue);
        moveDepth = other.moveDepth;
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

    public Evaluation getMoveValue() {
        return moveValue;
    }

    public void setMoveValue(Evaluation moveValue) {
        this.moveValue = moveValue;
    }

    @Override
    public String toString() {
        return "MinimaxMove{" +
                "move=" + move +
                ", moveValue=" + moveValue +
                ", moveDepth=" + moveDepth +
                '}';
    }
}
