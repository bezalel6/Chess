package ver31_fast_minimax.model_classes.eval_classes;

import ver31_fast_minimax.Controller;
import ver31_fast_minimax.model_classes.GameStatus;

import java.util.ArrayList;

public class Evaluation {
    public static final double WIN_EVAL = Double.MAX_VALUE, TIE_EVAL = 0;
    public static final int WINNING_SIDE = 0, LOSING_SIDE = 1;
    private static final int SENSITIVITY = (int) 1.E7;
    private double[] detailedEval;
    private ArrayList<Integer> initializedIndexes;
    private double eval;
    private GameStatus gameStatus;

    public Evaluation(double eval, GameStatus gameStatus) {
        initDetailedEval();
        this.eval = eval;
        this.gameStatus = gameStatus;
    }

    public Evaluation(int gameStatusType) {
        gameStatus = new GameStatus(gameStatusType);
    }

    public Evaluation(GameStatus gameStatus, int winningPlayer) {
        this.gameStatus = gameStatus;
        gameStatus.setPlayer(winningPlayer);

        switch (gameStatus.getGameStatusType()) {
            case WIN_OR_LOSS:
                eval = WIN_EVAL;
                break;
            case TIE:
                eval = TIE_EVAL;
                break;
            default:
                eval = 0;
                break;
        }
    }

    public Evaluation() {
        eval = 0;
        gameStatus = new GameStatus();
        initDetailedEval();
    }

    public Evaluation(Evaluation other) {
        copyEvaluation(other);
    }

    public Evaluation(boolean isMax) {
        this();
        eval = isMax ? -WIN_EVAL + 1 : WIN_EVAL - 1;
    }

    public void copyEvaluation(Evaluation other) {
        eval = other.eval;
        gameStatus = other.gameStatus;
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
        detailedEval = new double[Eval.NUM_OF_EVAL_PARAMETERS];
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

    @Override
    public String toString() {
        return "Eval{" +
                "eval=" + eval +
                ", gameStatus=" + gameStatus +
                ", detailedEval= " + getDetailedEvalStr() +
                '}';
    }

    private String getDetailedEvalStr() {
        StringBuilder ret = new StringBuilder();
        if (initializedIndexes == null)
            return "";
        for (int index : initializedIndexes) {
            ret.append("\n").append(Eval.EVAL_PARAMETERS_NAMES[index]).append(": ").append(detailedEval[index]).append(Controller.HIDE_PRINT);
        }
        return ret.toString();
    }

    public void flipSide() {
        eval = -eval;
        gameStatus.flip();
    }
}