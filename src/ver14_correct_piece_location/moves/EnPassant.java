package ver14_correct_piece_location.moves;

import ver14_correct_piece_location.Board;
import ver14_correct_piece_location.Location;

public class EnPassant extends SpecialMove {
    public EnPassant(Location from, Location to, Board board) {
        super(new Move(from, to, true, board), SpecialMoveType.CAPTURING_EN_PASSANT);
    }
}
