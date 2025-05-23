package ver15_new_piece_tables.moves;

import ver15_new_piece_tables.types.Piece;

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
