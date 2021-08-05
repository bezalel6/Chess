package ver13_FEN.moves;

import ver13_FEN.Location;

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
