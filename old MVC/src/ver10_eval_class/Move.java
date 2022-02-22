package ver10_eval_class;

import ver10_eval_class.types.Piece;

class MinimaxMove {
    private ver10_eval_class.Move move;
    private ver10_eval_class.EvalValue moveValue;

    public MinimaxMove(ver10_eval_class.Move move, ver10_eval_class.EvalValue moveValue) {
        this.move = move;
        this.moveValue = moveValue;
    }

    public MinimaxMove(ver10_eval_class.MinimaxMove other) {
        moveValue = new ver10_eval_class.EvalValue(other.moveValue);
    }

    public ver10_eval_class.Move getMove() {
        return move;
    }

    public void setMove(ver10_eval_class.Move move) {
        this.move = move;
    }

    public ver10_eval_class.EvalValue getMoveValue() {
        return moveValue;
    }

    public void setMoveValue(ver10_eval_class.EvalValue moveValue) {
        this.moveValue = moveValue;
    }

    @Override
    public String toString() {
        return "MinimaxMove{" +
                "move=" + move +
                ", moveValue=" + moveValue +
                '}';
    }
}

class PromotionMove {
    private ver10_eval_class.Move move;
    private ver10_eval_class.types.Piece.types promotingTo;

    public PromotionMove(ver10_eval_class.Move move, ver10_eval_class.types.Piece.types promotingTo) {
        this.move = move;
        this.promotingTo = promotingTo;
    }

    public ver10_eval_class.Move getMove() {
        return move;
    }

    public void setMove(ver10_eval_class.Move move) {
        this.move = move;
    }

    public ver10_eval_class.types.Piece.types getPromotingTo() {
        return promotingTo;
    }

    public void setPromotingTo(ver10_eval_class.types.Piece.types promotingTo) {
        this.promotingTo = promotingTo;
    }
}

public class Move {
    private Location movingTo, movingFrom;
    private boolean isCapturing;
    private Piece piece;
    private Location enPassantActualCapturingLoc;
    private SpecialMoves specialMove;
    private PromotionMove promotionMove;


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

    public PromotionMove getPromotionMove() {
        return promotionMove;
    }

    public void setPromotionMove(PromotionMove promotionMove) {
        this.promotionMove = promotionMove;
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
