package ver14.SharedClasses.Game.evaluation;

import ver14.SharedClasses.Game.PlayerColor;

import java.io.Serializable;
import java.util.ArrayList;

public class Evaluation implements Serializable {
    public static final double WIN_EVAL = Double.MAX_VALUE, TIE_EVAL = 0;
    public static final int WINNING_SIDE = 0, LOSING_SIDE = 1;
    private final ArrayList<EvaluationDetail> detailedEval;
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
        this.eval = eval;
        this.gameStatus = gameStatus;
        this.evaluationFor = evaluationFor;
        detailedEval = new ArrayList<>();
    }

    ;

    public Evaluation(PlayerColor evaluationFor) {
        this(0, GameStatus.gameGoesOn(), evaluationFor);
    }

    public Evaluation(Evaluation other) {
        this(other.eval, other.gameStatus, other.evaluationFor);
        detailedEval.addAll(other.detailedEval);
        this.evaluationDepth = other.evaluationDepth;
    }

    public static Evaluation book() {
        //fixme
        return new Evaluation(PlayerColor.WHITE) {{
            addDetail(EvaluationParameters.STOCKFISH_SAYS, 1000000);
        }};
    }

    public void addDetail(EvaluationParameters parm, double value) {
        eval += value;
        detailedEval.add(new EvaluationDetail(parm, value));
    }

    public Integer getEvaluationDepth() {
        return evaluationDepth;
    }

    public void setEvaluationDepth(Integer evaluationDepth) {
        this.evaluationDepth = evaluationDepth;
        gameStatus.setDepth(evaluationDepth);
    }

    public boolean isGameOver() {
        return gameStatus.isGameOver();
    }

    public boolean isCheck() {
        return gameStatus.isCheck();
    }

    public boolean isGreaterThan(Evaluation other) {
        return other.eval < this.eval || (eval == other.eval && ((eval > 0 && evaluationDepth < other.evaluationDepth) || (eval < 0 && evaluationDepth > other.evaluationDepth)));
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
        for (var v : detailedEval) {
            ret.append("\n").append(v);
        }
        return ret.toString();
    }

    public PlayerColor getEvaluationFor() {
        return evaluationFor;
    }

    public Evaluation setPerspective(PlayerColor playerColor) {
        if (evaluationFor != playerColor)
            flipEval();
        evaluationFor = playerColor;
        return this;
    }

    public void flipEval() {
        eval = -eval;
    }

    public void print() {
        System.out.println(this);
    }

    public record EvaluationDetail(EvaluationParameters parm, double eval) implements Serializable {
        @Override
        public String toString() {
            return parm + ": " + eval;
        }
    }

}
