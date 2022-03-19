package ver14.Model.MoveGenerator;

public final class GenerationSettings {

    public static final GenerationSettings defaultSettings = new GenerationSettings(true, false);
    public static final GenerationSettings anyLegalMove = new GenerationSettings(true, true);
    public final boolean legalize;
    public final boolean anyLegal;


    public GenerationSettings(boolean legalize, boolean anyLegal) {
        this.legalize = legalize;
        this.anyLegal = anyLegal;
    }
}
