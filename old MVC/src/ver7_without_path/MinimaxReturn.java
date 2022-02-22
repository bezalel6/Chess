package ver7_without_path;

public class MinimaxReturn {
    private Move move;
    private int depth;
    private double value;

    public MinimaxReturn(Move move, double value, int depth) {
        this.move = move;
        this.depth = depth;
        this.value = value;
    }

    public MinimaxReturn(Move move, double value) {
        this.move = move;
        this.value = value;
        depth = Integer.MAX_VALUE;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MinimaxReturn{" +
                "move=" + move +
                ", value=" + value +
                '}';
    }
}
