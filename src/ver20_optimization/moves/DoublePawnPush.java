package ver20_optimization.moves;

import ver20_optimization.Location;

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
