package ver9_Dialogs;

import ver9_Dialogs.types.Piece;

public class Move {
    private Location movingTo, movingFrom;
    private boolean isCapturing;
    private Piece piece;
    private Location enPassantActualCapturingLoc;
    private SpecialMoves specialMove;

    public Move(Piece piece, Location movingTo, boolean isCapturing) {
        this.movingTo = movingTo;
        this.movingFrom = piece.getLoc();
        this.isCapturing = isCapturing;
        this.piece = piece;
        specialMove = SpecialMoves.NONE;
    }

    public Move(Piece piece, Location movingTo, Location enPassantActualCapturingLoc) {
        this.movingFrom = piece.getLoc();
        this.movingTo = movingTo;
        this.piece = piece;
        this.enPassantActualCapturingLoc = enPassantActualCapturingLoc;
        isCapturing = true;
        specialMove = SpecialMoves.EN_PASSANT;
    }

    public Location getMovingFrom() {
        return movingFrom;
    }

    public Location getEnPassantActualCapturingLoc() {
        return enPassantActualCapturingLoc;
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
                "movingTo=" + movingTo +
                ", movingFrom=" + movingFrom +
                ", isCapturing=" + isCapturing +
                ", piece=" + piece +
                '}';
    }

    public enum SpecialMoves {SHORT_CASTLE, LONG_CASTLE, PROMOTION, EN_PASSANT, NONE}
}
