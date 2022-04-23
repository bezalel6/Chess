package ver14.Model.MoveGenerator;

import org.intellij.lang.annotations.MagicConstant;

/*
 * GenerationSettings
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * GenerationSettings -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * GenerationSettings -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

@MagicConstant(valuesFromClass = GenerationSettings.class)
public @interface GenerationSettings {
    int LEGALIZE = 1;
    int ANY_LEGAL = 2;
    int EVAL = 4 | LEGALIZE;
    int ANNOTATE = 8 | EVAL | LEGALIZE;

    int QUIESCE = 16 | LEGALIZE;
}
