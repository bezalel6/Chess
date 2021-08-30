package ver16_calculating_pinned_pieces.moves;

import ver16_calculating_pinned_pieces.Location;

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
