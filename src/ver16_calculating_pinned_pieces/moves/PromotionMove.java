package ver16_calculating_pinned_pieces.moves;

import ver16_calculating_pinned_pieces.types.Piece;

public class PromotionMove extends SpecialMove {
    private Piece.PieceTypes promotingTo;

    public PromotionMove(Piece.PieceTypes promotingTo, Move move) {
        super(move, SpecialMoveType.PROMOTION);
        this.promotingTo = promotingTo;
    }

    public String getAnnotation() {
        return super.getAnnotation() + "=" + promotingTo;
    }

    public Piece.PieceTypes getPromotingTo() {
        return promotingTo;
    }

    public void setPromotingTo(Piece.PieceTypes promotingTo) {
        this.promotingTo = promotingTo;
    }
}
