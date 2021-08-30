package ver17_new_movement.moves;

import ver17_new_movement.types.Piece;

public class PromotionMove extends SpecialMove {
    private int promotingTo;

    public PromotionMove(int promotingTo, Move move) {
        super(move, SpecialMoveType.PROMOTION);
        this.promotingTo = promotingTo;
    }

    public String getAnnotation() {
        return super.getAnnotation() + "=" + promotingTo;
    }

    public int getPromotingTo() {
        return promotingTo;
    }

    public void setPromotingTo(int promotingTo) {
        this.promotingTo = promotingTo;
    }
}
