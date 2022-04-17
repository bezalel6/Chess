package ver14.Model.MoveGenerator;

import org.intellij.lang.annotations.MagicConstant;

@MagicConstant(valuesFromClass = GenerationSettings.class)
public @interface GenerationSettings {
    int LEGALIZE = 1;
    int ANY_LEGAL = 2;
    int EVAL = 4 | LEGALIZE;
    int ANNOTATE = 8 | EVAL | LEGALIZE;

    int QUIESCE = 16 | LEGALIZE;
}
