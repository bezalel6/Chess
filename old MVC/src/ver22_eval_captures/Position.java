package ver22_eval_captures;

public class Position {
    private String name, FEN;

    public Position(String name, String FEN) {
        this.name = name;
        this.FEN = FEN;
    }

    public String getName() {
        return name;
    }

    public String getFen() {
        return FEN;
    }
}
