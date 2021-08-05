package ver14_pieceLocation.moves;

import ver14_pieceLocation.BoardEval;

public class MinimaxMove {
    private Move move;
    private BoardEval moveValue;
    private int moveDepth;

    public MinimaxMove(Move move, BoardEval moveValue, int moveDepth) {
        this.move = move;
        this.moveDepth = moveDepth;
        this.moveValue = moveValue;
    }

    public MinimaxMove(MinimaxMove other) {
        move = new Move(other.move);
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
        this.move = new Move(move);
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
