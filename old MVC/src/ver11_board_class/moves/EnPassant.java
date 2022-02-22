package ver11_board_class.moves;

import ver11_board_class.Board;
import ver11_board_class.Location;

public class EnPassant extends SpecialMove {
    private Location capturingPieceActualLocation;
    private Move capturedMoveToBeCaptured;

    public EnPassant(Location from, Location to, SpecialMoveType moveType, Location capturingPieceActualLocation, Board board) {
        super(new Move(from, to, true, board), moveType, board);
        this.capturingPieceActualLocation = capturingPieceActualLocation;
        capturedMoveToBeCaptured = new Move(capturingPieceActualLocation, to, false, board);
    }

    public Location getCapturingPieceActualLocation() {
        return capturingPieceActualLocation;
    }

    public Move getCapturedMoveToBeCaptured() {
        return capturedMoveToBeCaptured;
    }
}
