package ver16_calculating_pinned_pieces.moves;

import ver16_calculating_pinned_pieces.Board;
import ver16_calculating_pinned_pieces.Location;

public class EnPassant extends SpecialMove {
    public EnPassant(Location from, Location to, Board board) {
        super(new Move(from, to, true, board), SpecialMoveType.CAPTURING_EN_PASSANT);
    }

    public EnPassant(Move move) {
        super(move, SpecialMoveType.CAPTURING_EN_PASSANT);
    }
}
