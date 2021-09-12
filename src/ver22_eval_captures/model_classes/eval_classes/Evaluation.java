package ver22_eval_captures.model_classes.eval_classes;

import ver22_eval_captures.Error;
import ver22_eval_captures.model_classes.GameStatus;

public class Evaluation {
    private static final double winEval = Double.MAX_VALUE, tieEval = 0;

    private double eval;
    private GameStatus gameStatus;

    public Evaluation(double eval, GameStatus gameStatus) {
        this.eval = eval;
        this.gameStatus = gameStatus;
    }

    public Evaluation(double eval) {
        this.eval = eval;
        gameStatus = GameStatus.GAME_GOES_ON;
    }

    public Evaluation(GameStatus gameStatus, int side) {
        this.gameStatus = gameStatus;
        gameStatus.setSide(side);
        double res;
        switch (gameStatus) {
            case TIMED_OUT:
            case CHECKMATE:
                res = winEval * gameStatus.getSideMult();
                break;
            case STALEMATE:
            case THREE_FOLD_REPETITION:
            case INSUFFICIENT_MATERIAL:
            case TIME_OUT_VS_INSUFFICIENT_MATERIAL:
            case FIFTY_MOVE_RULE:
                res = tieEval;
                break;
            default:
                Error.error("game status not supported");
                res = 0;
                break;
        }
        eval = res;
    }

    public Evaluation() {
        this.gameStatus = GameStatus.GAME_GOES_ON;
    }

    public Evaluation(Evaluation other) {
        eval = other.eval;
        gameStatus = other.gameStatus;
    }

    /**
     * side not relevant (draw)
     *
     * @param gameStatus
     */
    public Evaluation(GameStatus gameStatus) {
        this(gameStatus, GameStatus.SIDE_NOT_RELEVANT);
    }

    public boolean isGreaterThan(Evaluation other) {
        return other.eval < this.eval;
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

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public boolean isGameOver() {
        return gameStatus != GameStatus.GAME_GOES_ON;
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
                '}';
    }
}
