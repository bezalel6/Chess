package ver17_new_movement.moves;

import ver17_new_movement.Board;
import ver17_new_movement.Location;

public class EnPassant extends SpecialMove {
    public EnPassant(Location from, Location to, Board board) {
        super(new Move(from, to, true, board), SpecialMoveType.CAPTURING_EN_PASSANT);
    }

    public EnPassant(Move move) {
        super(move, SpecialMoveType.CAPTURING_EN_PASSANT);
    }
}
