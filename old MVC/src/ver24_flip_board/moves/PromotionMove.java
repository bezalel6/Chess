package ver24_flip_board.moves;

import ver24_flip_board.model_classes.pieces.Piece;

public class PromotionMove extends Move {
    private int promotingTo;

    public PromotionMove(int promotingTo, Move move) {
        super(move);
        this.promotingTo = promotingTo;
        moveAnnotation.setPromotion(getPromotionString());
    }

    public PromotionMove(PromotionMove move) {
        super(move);
        this.promotingTo = move.promotingTo;
    }

    public String getPromotionString() {
        return "=" + Piece.PIECES_NAMES[promotingTo];
    }

    public int getPromotingTo() {
        return promotingTo;
    }

    public void setPromotingTo(int promotingTo) {
        this.promotingTo = promotingTo;
    }
}
