package ver5;

import ver5.types.Path;
import ver5.types.Piece;

public class Move {
    private Path path;
    private Piece piece;

    public Move(Path path, Piece piece) {
        this.path = path;
        this.piece = piece;
    }

    public Path getPath() {
        return path;
    }

    public Piece getPiece() {
        return piece;
    }
    

    @Override
    public String toString() {
        return "Move{" +
                "path=" + path +
                ", piece=" + piece +
                '}';
    }
}
