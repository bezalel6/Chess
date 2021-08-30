package ver17_new_movement.moves;

import ver17_new_movement.Location;

public class DoublePawnPush extends Move {
    private Location enPassantTargetSquare;

    public DoublePawnPush(Move move, Location enPassantTargetSquare) {
        super(move);
        this.enPassantTargetSquare = enPassantTargetSquare;
    }

    public Location getEnPassantTargetSquare() {
        return enPassantTargetSquare;
    }
}
