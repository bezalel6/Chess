package ver28_minimax_levels.model_classes.moves;

import ver28_minimax_levels.Location;
import ver28_minimax_levels.model_classes.Board;

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
