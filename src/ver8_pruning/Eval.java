package ver8_pruning;

enum GameStatus {CHECKMATE, INSUFFICIENT_MATERIAL, OPPONENT_TIMED_OUT, TIME_OUT_VS_INSUFFICIENT_MATERIAL, STALEMATE, REPETITION, GAME_GOES_ON, LOSS}

public class Eval {
    private double eval;
    private double winEval = 1000, tieEval = 0;
    private GameStatus gameStatus;

    public Eval(double eval, GameStatus gameStatus) {
        this.eval = eval;
        this.gameStatus = gameStatus;
    }

    public Eval(double eval) {
        this.eval = eval;
        gameStatus = GameStatus.GAME_GOES_ON;
    }

    public Eval(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        double res;
        switch (gameStatus) {
            case OPPONENT_TIMED_OUT:
            case CHECKMATE:
                res = winEval;
                break;
            case LOSS:
                res = -winEval;
                break;
            case STALEMATE:
            case REPETITION:
            case INSUFFICIENT_MATERIAL:
            case TIME_OUT_VS_INSUFFICIENT_MATERIAL:
                res = tieEval;
                break;
            default:
                res = 0;
        }
        eval = res;
    }

    public Eval() {
        this.gameStatus = GameStatus.GAME_GOES_ON;
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
    public String toString() {
        return "Eval{" +
                "eval=" + eval +
                ", gameStatus=" + gameStatus +
                '}';
    }
}

