package ver21_square_class.moves;

import ver21_square_class.Location;

public class DoublePawnPush extends Move {
    private Location enPassantTargetSquare;


    public DoublePawnPush(Move move, Location enPassantTargetSquare) {
        super(move);
        this.enPassantTargetSquare = enPassantTargetSquare;
    }

    public DoublePawnPush(DoublePawnPush move) {
        super(move);
        this.enPassantTargetSquare = move.enPassantTargetSquare;
    }

    public Location getEnPassantTargetSquare() {
        return enPassantTargetSquare;
    }
}
