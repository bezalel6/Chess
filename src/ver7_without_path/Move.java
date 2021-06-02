package ver7_without_path;

import ver7_without_path.types.Piece;

public class Move {
    private Location movingTo;
    private boolean isCapturing;
    private Piece piece;
    private SpecialMoves specialMove;

    public Move(Piece piece, Location movingTo, boolean isCapturing) {
        this.movingTo = movingTo;
        this.isCapturing = isCapturing;
        this.piece = piece;
        specialMove = SpecialMoves.NONE;
    }

    public SpecialMoves getSpecialMove() {
        return specialMove;
    }

    public void setSpecialMove(SpecialMoves specialMove) {
        this.specialMove = specialMove;
    }

    public Location getMovingTo() {
        return movingTo;
    }

    public Piece getPiece() {
        return piece;
    }

    public boolean isCapturing() {
        return isCapturing;
    }

    @Override
    public String toString() {
        return "Move{" +
                "moving to =" + movingTo +
                ", piece=" + piece +
                '}';
    }


    public enum SpecialMoves {SHORT_CASTLE, LONG_CASTLE, PROMOTION, EN_PASSANT, NONE}
}
