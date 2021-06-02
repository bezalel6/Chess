package ver11_board_class.moves;

import ver11_board_class.BoardEval;

public class MinimaxMove {
    private Move move;
    private BoardEval moveValue;

    public MinimaxMove(Move move, BoardEval moveValue) {
        this.move = move;
        this.moveValue = moveValue;
    }

    public MinimaxMove(MinimaxMove other) {
        move = new Move(other.move, other.move.getBoard());
        moveValue = new BoardEval(other.moveValue);
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
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
                '}';
    }
}
