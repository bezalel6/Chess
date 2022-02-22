package ver6.SharedClasses.evaluation;

import ver6.SharedClasses.Hashable;
import ver6.SharedClasses.PlayerColor;

import java.io.Serializable;
import java.util.ArrayList;

public class Evaluation implements Serializable, Hashable {
    public static final double WIN_EVAL = Double.MAX_VALUE, TIE_EVAL = 0;
    public static final int WINNING_SIDE = 0, LOSING_SIDE = 1;
    private double[] detailedEval;
    private ArrayList<Integer> initializedIndexes;
    private double eval;
    private GameStatus gameStatus;
    private PlayerColor evaluationFor;
    private Integer evaluationDepth = null;

    public Evaluation(GameStatus gameStatus, PlayerColor evaluationFor) {
        this(0, gameStatus, evaluationFor);
        switch (gameStatus.getGameStatusType()) {
            case WIN_OR_LOSS -> eval = -WIN_EVAL;
            case TIE -> eval = TIE_EVAL;
            default -> eval = 0;
        }
    }

    public Evaluation(double eval, GameStatus gameStatus, PlayerColor evaluationFor) {
        initDetailedEval();
        this.eval = eval;
        this.gameStatus = gameStatus;
        this.evaluationFor = evaluationFor;
    }

    private void initDetailedEval() {
        detailedEval = new double[EvaluationParameters.NUM_OF_EVAL_PARAMETERS];
        initializedIndexes = new ArrayList<>();
    }

    public Evaluation(PlayerColor evaluationFor) {
        this(0, GameStatus.gameGoesOn(), evaluationFor);
    }

    public Evaluation(Evaluation other) {
        copyEvaluation(other);
    }

    public void copyEvaluation(Evaluation other) {
        eval = other.eval;
        gameStatus = other.gameStatus;
        this.evaluationFor = other.evaluationFor;
        this.evaluationDepth = other.evaluationDepth;
        if (!other.isGameOver()) {
            detailedEval = other.detailedEval.clone();
            initializedIndexes = new ArrayList<>();
            initializedIndexes.addAll(other.initializedIndexes);
        }
    }

    public boolean isGameOver() {
        return gameStatus.isGameOver();
    }

    public static Evaluation book() {
        //fixme
        return new Evaluation(PlayerColor.WHITE);
    }

    public void setEvaluationDepth(Integer evaluationDepth) {
        this.evaluationDepth = evaluationDepth;
        gameStatus.setDepth(evaluationDepth);
    }

    public boolean isCheck() {
        return gameStatus.isCheck();
    }

    public boolean isGreaterThan(Evaluation other) {
        return other.eval < this.eval;
    }

    public void addDetail(int evalType, double value) {
        eval += value;
        addToList(evalType, value);
    }

    private void addToList(int evalType, double value) {
        detailedEval[evalType] = value;
        if (!initializedIndexes.contains(evalType))
            initializedIndexes.add(evalType);
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

    public double getEval() {
        return eval;
    }

    public void setEval(double eval) {
        this.eval = eval;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evaluation boardEval = (Evaluation) o;
        return Double.compare(boardEval.eval, eval) == 0 && gameStatus == boardEval.gameStatus;
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

    public PlayerColor getEvaluationFor() {
        return evaluationFor;
    }

    public void setPerspective(PlayerColor playerColor) {
        if (evaluationFor != playerColor)
            flipEval();
        evaluationFor = playerColor;
    }

    public void flipEval() {
        eval = -eval;
    }

}
