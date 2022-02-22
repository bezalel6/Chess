package ver12_myJbutton.moves;

import ver12_myJbutton.Board;
import ver12_myJbutton.Location;

public class EnPassant extends SpecialMove {
    private Location capturingPieceActualLocation;
    private Move capturedMoveToBeCaptured;

    public EnPassant(Location from, Location to, SpecialMoveType moveType, Location capturingPieceActualLocation, Board board) {
        super(new Move(from, to, true, board), moveType);
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
