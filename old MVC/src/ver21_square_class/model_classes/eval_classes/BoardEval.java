package ver21_square_class.model_classes.eval_classes;

import ver21_square_class.model_classes.GameStatus;

public class BoardEval {
    private static final double winEval = 1000, tieEval = 0;
    private double eval;
    private GameStatus gameStatus;

    public BoardEval(double eval, GameStatus gameStatus) {
        this.eval = eval;
        this.gameStatus = gameStatus;
    }

    public BoardEval(double eval) {
        this.eval = eval;
        gameStatus = GameStatus.GAME_GOES_ON;
    }

    public BoardEval(GameStatus gameStatus, int side) {
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
                res = tieEval;
                break;
            default:
                res = 0;
        }
        eval = res;
    }

    public BoardEval() {
        this.gameStatus = GameStatus.GAME_GOES_ON;
    }

    public BoardEval(BoardEval other) {
        eval = other.eval;
        gameStatus = other.gameStatus;
    }

    /**
     * side not relevant (draw)
     *
     * @param gameStatus
     */
    public BoardEval(GameStatus gameStatus) {
        this(gameStatus, GameStatus.SIDE_NOT_RELEVANT);
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
        BoardEval boardEval = (BoardEval) o;
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
