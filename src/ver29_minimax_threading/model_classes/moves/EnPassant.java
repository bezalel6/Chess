package ver29_minimax_threading.model_classes.moves;

import ver29_minimax_threading.Location;
import ver29_minimax_threading.model_classes.Board;

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
