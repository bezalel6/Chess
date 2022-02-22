package ver18_icon_manager.moves;

import ver18_icon_manager.Board;
import ver18_icon_manager.Location;

public class EnPassant extends Move {
    public EnPassant(Location from, Location to, Board board) {
        super(new Move(from, to, true, board));
    }

    public EnPassant(Move move) {
        super(move);
    }
}
