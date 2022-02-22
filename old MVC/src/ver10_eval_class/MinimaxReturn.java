package ver10_eval_class;

public class MinimaxReturn {
    private Move move;
    private int depth;
    private EvalValue evalValue;

    public MinimaxReturn(Move move, EvalValue value, int depth) {
        this.move = move;
        this.depth = depth;
        this.evalValue = value;
    }

    public MinimaxReturn(Move move, EvalValue value) {
        this.move = move;
        this.evalValue = value;
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

    public EvalValue getEval() {
        return evalValue;
    }

    public void setEval(EvalValue eval) {
        this.evalValue = eval;
    }

    @Override
    public String toString() {
        return "MinimaxReturn{" +
                "move=" + move +
                ", depth=" + depth +
                ", value=" + evalValue +
                '}';
    }
}
