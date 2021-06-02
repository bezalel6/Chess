package ver11_board_class.moves;

import ver11_board_class.Board;
import ver11_board_class.types.Piece;

public class PromotionMove extends SpecialMove {
    private Piece.types promotingTo;

    public PromotionMove(Piece.types promotingTo, Move move, Board board) {
        super(move, SpecialMoveType.PROMOTION, "=" + promotingTo, board);
        this.promotingTo = promotingTo;
    }

    public Piece.types getPromotingTo() {
        return promotingTo;
    }

    public void setPromotingTo(Piece.types promotingTo) {
        this.promotingTo = promotingTo;
    }
}
