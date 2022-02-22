package ver31_fast_minimax.model_classes.moves;

import ver31_fast_minimax.Location;
import ver31_fast_minimax.model_classes.Board;

public class EnPassant extends Move {
    public EnPassant(Location movingFrom, Location movingTo, int movingPlayer, int movingPieceType, int capturingPieceType, Board board) {
        super(movingFrom, movingTo, movingPlayer, movingPieceType, capturingPieceType);
    }

    public EnPassant(Move other, int capturing) {
        super(other);

    }

    public EnPassant(EnPassant move) {
        super(move);
    }
}
