package OLD_ver14_pieceLocation.moves;

import OLD_ver14_pieceLocation.types.Piece;

public class PromotionMove extends SpecialMove {
    private Piece.types promotingTo;

    public PromotionMove(Piece.types promotingTo, Move move) {
        super(move, SpecialMoveType.PROMOTION);
        this.promotingTo = promotingTo;
    }

    public String getAnnotation() {
        return super.getAnnotation() + "=" + promotingTo;
    }

    public Piece.types getPromotingTo() {
        return promotingTo;
    }

    public void setPromotingTo(Piece.types promotingTo) {
        this.promotingTo = promotingTo;
    }
}
