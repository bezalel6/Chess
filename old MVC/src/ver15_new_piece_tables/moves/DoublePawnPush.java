package ver15_new_piece_tables.moves;

import ver15_new_piece_tables.Location;

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
