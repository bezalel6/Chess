package ver14_correct_piece_location.moves;

import ver14_correct_piece_location.Location;

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
