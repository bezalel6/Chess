package ver15_new_piece_tables.moves;

import ver15_new_piece_tables.Board;
import ver15_new_piece_tables.Location;

public class EnPassant extends SpecialMove {
    public EnPassant(Location from, Location to, Board board) {
        super(new Move(from, to, true, board), SpecialMoveType.CAPTURING_EN_PASSANT);
    }

    public EnPassant(Move move) {
        super(move, SpecialMoveType.CAPTURING_EN_PASSANT);
    }
}
