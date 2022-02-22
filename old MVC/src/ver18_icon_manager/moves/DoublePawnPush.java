package ver18_icon_manager.moves;

import ver18_icon_manager.Location;

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
