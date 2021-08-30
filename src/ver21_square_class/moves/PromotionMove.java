package ver21_square_class.moves;

import ver21_square_class.types.Piece;

public class PromotionMove extends Move {
    private int promotingTo;

    public PromotionMove(int promotingTo, Move move) {
        super(move);
        this.promotingTo = promotingTo;
    }

    public PromotionMove(PromotionMove move) {
        super(move);
        this.promotingTo = move.promotingTo;
    }

    public String getAnnotation() {
        return super.getAnnotation() + "=" + Piece.PIECES_NAMES[promotingTo];
    }

    public int getPromotingTo() {
        return promotingTo;
    }

    public void setPromotingTo(int promotingTo) {
        this.promotingTo = promotingTo;
    }

    void copyConstructor(Move move) {
        super.copyConstructor(move);
    }

}
