package ver21_square_class.moves;

import ver21_square_class.model_classes.eval_classes.BoardEval;

public class MinimaxMove {
    private Move move;
    private BoardEval moveValue;
    private int moveDepth;

    public MinimaxMove(Move move, BoardEval moveValue, int moveDepth) {
        this.move = Move.copyMove(move);
        this.moveDepth = moveDepth;
        this.moveValue = moveValue;
    }


    public MinimaxMove(BoardEval moveValue) {
        this.moveValue = moveValue;
    }

    public MinimaxMove(MinimaxMove other) {
        move = Move.copyMove(other.move);
        moveValue = new BoardEval(other.moveValue);
        moveDepth = other.moveDepth;
    }

    public int getMoveDepth() {
        return moveDepth;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        //todo 🤮
        this.move = Move.copyMove(move);
    }

    public BoardEval getMoveValue() {
        return moveValue;
    }

    public void setMoveValue(BoardEval moveValue) {
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
