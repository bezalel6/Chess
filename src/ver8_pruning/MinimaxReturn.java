package ver8_pruning;

public class MinimaxReturn {
    private Move move;
    private int depth;
    private Eval eval;

    public MinimaxReturn(Move move, Eval value, int depth) {
        this.move = move;
        this.depth = depth;
        this.eval = value;
    }

    public MinimaxReturn(Move move, Eval value) {
        this.move = move;
        this.eval = value;
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

    public Eval getEval() {
        return eval;
    }

    public void setEval(Eval eval) {
        this.eval = eval;
    }

    @Override
    public String toString() {
        return "MinimaxReturn{" +
                "move=" + move +
                ", depth=" + depth +
                ", value=" + eval +
                '}';
    }
}
