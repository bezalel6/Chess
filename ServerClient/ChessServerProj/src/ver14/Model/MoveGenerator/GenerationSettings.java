package ver14.Model.MoveGenerator;

import org.intellij.lang.annotations.MagicConstant;


/**
 * Generation settings.
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
