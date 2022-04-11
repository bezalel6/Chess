package ver14.Model.MoveGenerator;

public final class GenerationSettings {

    public static final GenerationSettings defaultSettings = new GenerationSettings(true, false);
    public static final GenerationSettings anyLegalMove = new GenerationSettings(true, true);
    public static final GenerationSettings evalEachMove = new GenerationSettings(true, false, true);
    public static final GenerationSettings annotate = new GenerationSettings(true, false, true);
    public final boolean legalize;
    public final boolean anyLegal;
    public final boolean eval;


    public GenerationSettings(boolean legalize, boolean anyLegal) {
        this(legalize, anyLegal, false);
    }

    public GenerationSettings(boolean legalize, boolean anyLegal, boolean eval) {
        this.legalize = legalize;
        this.anyLegal = anyLegal;
        this.eval = eval;
    }
}
