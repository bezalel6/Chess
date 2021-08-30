package ver19_square_control.moves;

import ver19_square_control.Board;
import ver19_square_control.Location;

public class EnPassant extends Move {
    public EnPassant(Move move, int capturingPieceHash) {
        super(move, capturingPieceHash);
    }

    public EnPassant(EnPassant move) {
        super(move);
    }
}
