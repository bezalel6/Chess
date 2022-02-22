package ver6;

public class MinimaxReturn {
    Move move;
    double value;

    public MinimaxReturn(Move move, double value) {
        this.move = move;
        this.value = value;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Move getMove() {
        return move;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "MinimaxReturn{" +
                "move=" + move +
                ", value=" + value +
                '}';
    }
}
