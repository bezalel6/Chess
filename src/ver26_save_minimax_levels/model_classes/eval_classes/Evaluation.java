package ver26_save_minimax_levels.model_classes.eval_classes;

import ver26_save_minimax_levels.Controller;
import ver26_save_minimax_levels.MyError;
import ver26_save_minimax_levels.model_classes.GameStatus;
import ver26_save_minimax_levels.moves.MoveAnnotation;

import java.util.ArrayList;

public class Evaluation {
    private static final double WIN_EVAL = Double.MAX_VALUE, TIE_EVAL = 0;
    private static final int SENSITIVITY = 100000000;
    private double[] detailedEval;
    private ArrayList<Integer> initializedIndexes;
    private double eval;
    private GameStatus gameStatus;

    public Evaluation(double eval, GameStatus gameStatus) {
        initDetailedEval();
        this.eval = eval;
        this.gameStatus = gameStatus;
    }

    public Evaluation(double eval) {
        initDetailedEval();
        this.eval = eval;
        gameStatus = GameStatus.GAME_GOES_ON;
    }

    public Evaluation(GameStatus gameStatus, int side) {
        this.gameStatus = gameStatus;
        gameStatus.setSide(side);
        double res;

        switch (gameStatus.getGameStatusType()) {
            case MoveAnnotation.WIN_OR_LOSS:
                res = WIN_EVAL * gameStatus.getSideMult();
                break;
            case MoveAnnotation.TIE:
                res = TIE_EVAL;
                break;
            default:
                MyError.error("not game over status");
                res = 0;
                break;
        }
        eval = res;
    }

    public Evaluation() {
        initDetailedEval();
        this.gameStatus = GameStatus.GAME_GOES_ON;
    }

    public Evaluation(Evaluation other) {
        copyEvaluation(other);
    }

    /**
     * side not relevant (draw)
     *
     * @param gameStatus
     */
    public Evaluation(GameStatus gameStatus) {
        this(gameStatus, GameStatus.SIDE_NOT_RELEVANT);
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
        value *= SENSITIVITY;
        value = Math.floor(value);
        value /= SENSITIVITY;
        detailedEval[evalType] = value;
        eval += value;
        if (!initializedIndexes.contains(evalType))
            initializedIndexes.add(evalType);
    }

    public double getEval() {
        return eval;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public boolean isGameOver() {
        return gameStatus != GameStatus.GAME_GOES_ON && gameStatus != GameStatus.CHECK;
    }

    public boolean didWin(int player) {
        return isGameOver() && gameStatus.getSide() == player;
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
                ", detailedEval=\n " + getDetailedEvalStr() +
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
}
