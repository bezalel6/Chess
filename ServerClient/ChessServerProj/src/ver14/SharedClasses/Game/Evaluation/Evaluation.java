package ver14.SharedClasses.Game.Evaluation;

import ver14.SharedClasses.Game.PlayerColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Evaluation
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * Evaluation -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * Evaluation -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class Evaluation implements Serializable {
    public static final int TIE_EVAL = 0;
    public static final int WIN_EVAL = Integer.MAX_VALUE;
    public static final int LOSS_EVAL = -WIN_EVAL;//חייב להיות -WIN כדי להמנע מגלישה כשכופלים ב-1
    private static final Map<GameStatus.GameStatusType, Integer> gameStatusEvalMap;
    private final static boolean MAKE_DETAILED = true;

    static {
        gameStatusEvalMap = new HashMap<>();

        for (var type : GameStatus.GameStatusType.values()) {
            gameStatusEvalMap.put(type, switch (type) {
                case WIN_OR_LOSS -> LOSS_EVAL;
                case TIE -> TIE_EVAL;
                default -> 0;
            });
        }

    }

    private final ArrayList<EvaluationDetail> detailedEval;
    private final GameStatus gameStatus;
    private int eval;
    private PlayerColor evaluationFor;
    private Integer evaluationDepth = null;

    public Evaluation(GameStatus gameStatus, PlayerColor evaluationFor) {
        this(gameStatusEvalMap.get(gameStatus.getGameStatusType()), gameStatus, evaluationFor);

    }

    public Evaluation(int eval, GameStatus gameStatus, PlayerColor evaluationFor) {
        this.eval = eval;
        this.gameStatus = gameStatus;
        this.evaluationFor = evaluationFor;
        detailedEval = MAKE_DETAILED ? new ArrayList<>() : null;
    }

    ;

    public Evaluation(PlayerColor evaluationFor) {
        this(0, GameStatus.gameGoesOn(), evaluationFor);
    }

    public Evaluation(Evaluation other) {
        this(other.eval, other.gameStatus, other.evaluationFor);
        this.evaluationDepth = other.evaluationDepth;
        if (MAKE_DETAILED)

            detailedEval.addAll(other.detailedEval);
    }

    public static Evaluation book() {
        //fixme
        return new Evaluation(PlayerColor.WHITE) {{
            addDetail(EvaluationParameters.STOCKFISH_SAYS, 1000000);
        }};
    }

    public void addDetail(EvaluationParameters parm, int value) {
        eval += value * parm.weight;
        if (MAKE_DETAILED)
            detailedEval.add(new EvaluationDetail(parm, value));
    }

    public void assertNotGameOver() {
        assert eval > LOSS_EVAL && eval < WIN_EVAL;
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

    public int getEval() {
        return eval;
    }

    public void setEval(int eval) {
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
        return "Evaluation{" +
                "detailedEval=" + detailedEval +
                ", eval=" + eval +
                ", gameStatus=" + gameStatus +
                ", evaluationFor=" + evaluationFor +
                ", evaluationDepth=" + evaluationDepth +
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

    public float convertFromCentipawns() {
        return ((float) eval) / 100;
    }

    public record EvaluationDetail(EvaluationParameters parm, double eval) implements Serializable {
        @Override
        public String toString() {
            return parm + ": " + eval;
        }
    }
}
