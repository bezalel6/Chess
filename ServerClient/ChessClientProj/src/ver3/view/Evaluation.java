package ver3.view;

import java.util.ArrayList;

public class Evaluation {
    public static final double WIN_EVAL = Double.MAX_VALUE, TIE_EVAL = 0;
    public static final int WINNING_SIDE = 0, LOSING_SIDE = 1;
    private double[] detailedEval;
    private ArrayList<Integer> initializedIndexes;
    private double eval;
    private GameStatus gameStatus;
    private Player evaluationFor;

    public Evaluation(double eval, GameStatus gameStatus) {
        initDetailedEval();
        this.eval = eval;
        this.gameStatus = gameStatus;
        this.evaluationFor = Player.WHITE;
    }

    public Evaluation(int gameStatusType) {
        this(0, new GameStatus(gameStatusType));
    }

    public Evaluation(GameStatus gameStatus) {
        this(0, gameStatus);
        switch (gameStatus.getGameStatusType()) {
            case WIN_OR_LOSS -> eval = WIN_EVAL;
            case TIE -> eval = TIE_EVAL;
            default -> eval = 0;
        }
    }

    public Evaluation() {
        this(0, new GameStatus());
    }

    public Evaluation(Evaluation other) {
        copyEvaluation(other);
    }

    public Evaluation(boolean isMax) {
        this();
        eval = isMax ? -WIN_EVAL : WIN_EVAL;
    }

    public void copyEvaluation(Evaluation other) {
        eval = other.eval;
        gameStatus = new GameStatus(other.gameStatus);
        this.evaluationFor = other.evaluationFor;
        if (!other.isGameOver()) {
            detailedEval = other.detailedEval.clone();
            initializedIndexes = new ArrayList<>();
            initializedIndexes.addAll(other.initializedIndexes);
        }
    }

    public boolean isCheck() {
        return gameStatus.isCheck();
    }

    public boolean isGreaterThan(Evaluation other) {
        return other.eval < this.eval;
    }

    private void initDetailedEval() {
        detailedEval = new double[EvaluationParameters.NUM_OF_EVAL_PARAMETERS];
        initializedIndexes = new ArrayList<>();
    }

    public void addDetail(int evalType, double value) {
        eval += value;
        addToList(evalType, value);
    }

    /**
     * add to the detailed eval without actually adding to the sum
     *
     * @param evalType
     * @param value
     */
    public void addDebugDetail(int evalType, double value) {
        addToList(evalType, value);
    }

    private void addToList(int evalType, double value) {
        detailedEval[evalType] = value;
        if (!initializedIndexes.contains(evalType))
            initializedIndexes.add(evalType);
    }

    public double getEval() {
        return eval;
    }

    public void setEval(double eval) {
        this.eval = eval;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatusType(GameStatus.GameStatusType type) {
        gameStatus.setGameStatusType(type);
    }

    public boolean isGameOver() {
        return gameStatus.isGameOver();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evaluation boardEval = (Evaluation) o;
        return Double.compare(boardEval.eval, eval) == 0 && gameStatus == boardEval.gameStatus;
    }

    public Player getEvaluationFor() {
        return evaluationFor;
    }

    @Override
    public String toString() {
        return "Eval{" +
                "eval=" + eval +
                ", gameStatus=" + gameStatus +
                ", detailedEval= " + getDetailedEvalStr() +
                ", evaluation for= " + evaluationFor.getName() +
                '}';
    }

    private String getDetailedEvalStr() {
        StringBuilder ret = new StringBuilder();
        if (initializedIndexes == null)
            return "";
        for (int index : initializedIndexes) {
            ret.append("\n").append(EvaluationParameters.EVAL_PARAMETERS_NAMES[index]).append(": ").append(detailedEval[index]);
        }
        return ret.toString();
    }

    public Evaluation getEvalForBlack() {
        Evaluation ret = new Evaluation(this);
        ret.evaluationFor = Player.BLACK;
        ret.flipEval();
        return ret;
    }

    public void flipEval() {
        eval = -eval;
    }
}
