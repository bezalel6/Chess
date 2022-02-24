package ver13.Model.MoveGenerator;

public final class GenerationSettings {

    public static final GenerationSettings defaultSettings = new GenerationSettings(true);
    private final boolean legalize;

    public GenerationSettings(boolean legalize) {
        this.legalize = legalize;
    }

    public boolean legalize() {
        return legalize;
    }

}
