package ver14.SharedClasses.Game.Evaluation;

import ver14.SharedClasses.Game.PlayerColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Evaluation.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Evaluation implements Serializable {
    /**
     * The constant TIE_EVAL.
     */
    public static final int TIE_EVAL = 0;
    /**
     * The constant WIN_EVAL.
     */
    public static final int WIN_EVAL = Integer.MAX_VALUE;
    /**
     * The constant LOSS_EVAL.
     */
    public static final int LOSS_EVAL = -WIN_EVAL;//חייב להיות -WIN כדי להמנע מגלישה כשכופלים ב-1
    /**
     * The constant gameStatusEvalMap.
     */
    private static final Map<GameStatus.GameStatusType, Integer> gameStatusEvalMap;
    /**
     * The constant MAKE_DETAILED.
     */
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

    /**
     * The Detailed eval.
     */
    private final ArrayList<EvaluationDetail> detailedEval;
    /**
     * The Game status.
     */
    private final GameStatus gameStatus;
    /**
     * The Eval.
     */
    private int eval;
    /**
     * The Evaluation for.
     */
    private PlayerColor evaluationFor;
    /**
     * The Evaluation depth.
     */
    private Integer evaluationDepth = null;

    /**
     * Instantiates a new Evaluation.
     *
     * @param gameStatus    the game status
     * @param evaluationFor the evaluation for
     */
    public Evaluation(GameStatus gameStatus, PlayerColor evaluationFor) {
        this(gameStatusEvalMap.get(gameStatus.getGameStatusType()), gameStatus, evaluationFor);

    }

    /**
     * Instantiates a new Evaluation.
     *
     * @param eval          the eval
     * @param gameStatus    the game status
     * @param evaluationFor the evaluation for
     */
    public Evaluation(int eval, GameStatus gameStatus, PlayerColor evaluationFor) {
        this.eval = eval;
        this.gameStatus = gameStatus;
        this.evaluationFor = evaluationFor;
        detailedEval = MAKE_DETAILED ? new ArrayList<>() : null;
    }

    ;

    /**
     * Instantiates a new Evaluation.
     *
     * @param evaluationFor the evaluation for
     */
    public Evaluation(PlayerColor evaluationFor) {
        this(0, GameStatus.gameGoesOn(), evaluationFor);
    }

    /**
     * Instantiates a new Evaluation.
     *
     * @param other the other
     */
    public Evaluation(Evaluation other) {
        this(other.eval, other.gameStatus, other.evaluationFor);
        this.evaluationDepth = other.evaluationDepth;
        if (MAKE_DETAILED)

            detailedEval.addAll(other.detailedEval);
    }

    /**
     * Book evaluation.
     *
     * @return the evaluation
     */
    public static Evaluation book() {
        //fixme
        return new Evaluation(PlayerColor.WHITE) {{
            addDetail(EvaluationParameters.STOCKFISH_SAYS, 1000000);
        }};
    }

    /**
     * Add detail.
     *
     * @param parm  the parm
     * @param value the value
     */
    public void addDetail(EvaluationParameters parm, int value) {
        eval += value * parm.weight;
        if (MAKE_DETAILED)
            detailedEval.add(new EvaluationDetail(parm, value));
    }

    /**
     * Assert not game over.
     */
    public void assertNotGameOver() {
        assert eval > LOSS_EVAL && eval < WIN_EVAL;
    }

    /**
     * Gets evaluation depth.
     *
     * @return the evaluation depth
     */
    public Integer getEvaluationDepth() {
        return evaluationDepth;
    }

    /**
     * Sets evaluation depth.
     *
     * @param evaluationDepth the evaluation depth
     */
    public void setEvaluationDepth(Integer evaluationDepth) {
        this.evaluationDepth = evaluationDepth;
        gameStatus.setDepth(evaluationDepth);
    }

    /**
     * Is game over boolean.
     *
     * @return the boolean
     */
    public boolean isGameOver() {
        return gameStatus.isGameOver();
    }

    /**
     * Is check boolean.
     *
     * @return the boolean
     */
    public boolean isCheck() {
        return gameStatus.isCheck();
    }

    /**
     * Is greater than boolean.
     *
     * @param other the other
     * @return the boolean
     */
    public boolean isGreaterThan(Evaluation other) {
        return other.eval < this.eval || (eval == other.eval && ((eval > 0 && evaluationDepth < other.evaluationDepth) || (eval < 0 && evaluationDepth > other.evaluationDepth)));
    }

    /**
     * Gets eval.
     *
     * @return the eval
     */
    public int getEval() {
        return eval;
    }

    /**
     * Sets eval.
     *
     * @param eval the eval
     */
    public void setEval(int eval) {
        this.eval = eval;
    }

    /**
     * Gets game status.
     *
     * @return the game status
     */
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evaluation boardEval = (Evaluation) o;
        return Double.compare(boardEval.eval, eval) == 0 && gameStatus == boardEval.gameStatus;
    }

    /**
     * To string string.
     *
     * @return the string
     */
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

    /**
     * Gets detailed eval str.
     *
     * @return the detailed eval str
     */
    private String getDetailedEvalStr() {
        StringBuilder ret = new StringBuilder();
        for (var v : detailedEval) {
            ret.append("\n").append(v);
        }
        return ret.toString();
    }

    /**
     * Gets evaluation for.
     *
     * @return the evaluation for
     */
    public PlayerColor getEvaluationFor() {
        return evaluationFor;
    }

    /**
     * Sets perspective.
     *
     * @param playerColor the player color
     * @return the perspective
     */
    public Evaluation setPerspective(PlayerColor playerColor) {
        if (evaluationFor != playerColor)
            flipEval();
        evaluationFor = playerColor;
        return this;
    }

    /**
     * Flip eval.
     */
    public void flipEval() {
        eval = -eval;
    }

    /**
     * Print.
     */
    public void print() {
        System.out.println(this);
    }

    /**
     * Convert from centipawns float.
     *
     * @return the float
     */
    public float convertFromCentipawns() {
        return ((float) eval) / 100;
    }

    /**
     * Evaluation detail.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public record EvaluationDetail(EvaluationParameters parm, double eval) implements Serializable {
        /**
         * To string string.
         *
         * @return the string
         */
        @Override
        public String toString() {
            return parm + ": " + eval;
        }
    }
}
