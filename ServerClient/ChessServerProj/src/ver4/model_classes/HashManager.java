package ver4.model_classes;

import ver4.model_classes.eval_classes.Eval;

public class HashManager {
    public static final boolean MOVE_GENERATION = false;
    public static final boolean ATTACKED_SQUARES = false;
    public static final boolean TRANSPOSITIONS = false;//not ready yet
    public static final boolean EVALUATIONS = false;
    public static final boolean GAME_OVER = false;
    //    true bc repetition hash
    public static final boolean HASH_BOARD = true;
    private static final boolean ANY = MOVE_GENERATION | ATTACKED_SQUARES | TRANSPOSITIONS | EVALUATIONS | GAME_OVER;

    public static void clearAll() {
        MoveGenerator.hashMap.clear();
        AttackedSquares.hashMap.clear();
        Minimax.transpositionsHashMap.clear();
        Eval.evaluationHashMap.clear();
        Eval.gameOverHashMap.clear();
    }
}
