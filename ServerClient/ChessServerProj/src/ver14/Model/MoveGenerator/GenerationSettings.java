package ver14.Model.MoveGenerator;

import org.intellij.lang.annotations.MagicConstant;


/**
 * Generation settings - a magic constant used to define the possible move-generation settings.<br/>
 * example usecase: when evaluating a position, the first thing to look for is game overs. the most common of which are: checkmates, and stalemates. in order to
 * efficiently check for them, the game looks for ANY legal move the current player can make. if one is found, it rules out both checkmate and stalemate.
 * since both of them require the player having no legal move.
 * <br/>
 * and so, instead of generating all moves for all the player's pieces, the generation setting {@link #ANY_LEGAL} is used. which stops generating the moment it finds a legal move.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
@MagicConstant(valuesFromClass = GenerationSettings.class)
public @interface GenerationSettings {
    /**
     * The constant LEGALIZE.
     */
    int LEGALIZE = 1;
    /**
     * The constant ANY_LEGAL.
     */
    int ANY_LEGAL = 2;
    /**
     * The constant EVAL.
     */
    int EVAL = 4 | LEGALIZE;
    /**
     * The constant ANNOTATE.
     */
    int ANNOTATE = 8 | EVAL | LEGALIZE;

    /**
     * The constant QUIESCE.
     */
    int QUIESCE = 16 | LEGALIZE;
}
