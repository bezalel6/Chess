package ver6;

import ver6.types.Path;
import ver6.types.Piece;

public class SpecialMove {
    public static final int SHORT_CASTLE = 0, LONG_CASTLE = 1, PROMOTION = 2, EN_PASSANT = 3;
    private Piece piece;
    private Path path;
    private int moveType;

    public SpecialMove(Piece piece, Path path, int moveType) {
        this.piece = piece;
        this.path = path;
        this.moveType = moveType;
    }

    public Piece getPiece() {
        return piece;
    }

    public Path getPath() {
        return path;
    }

    public int getMoveType() {
        return moveType;
    }
}
