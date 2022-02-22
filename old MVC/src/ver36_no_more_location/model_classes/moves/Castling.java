package ver36_no_more_location.model_classes.moves;

import ver36_no_more_location.Location;
import ver36_no_more_location.model_classes.CastlingAbility;

public class Castling extends Move {
    private static final String[] CASTLING_ANNOTATION = {"O-O", "O-O-O"};
    private final int side;

    public Castling(Location movingFrom, Location movingTo, int side) {
        super(movingFrom, movingTo);
        this.side = side;
        Location rookOrigin = Location.getLoc(movingFrom.getRow(), CastlingAbility.ROOK_STARTING_COL[side]);
        Location rookDestination = Location.getLoc(movingFrom.getRow(), CastlingAbility.CASTLED_ROOK_COL[side]);
        assert rookOrigin != null && rookDestination != null;
        intermediateMove = new BasicMove(rookOrigin, rookDestination);
        moveAnnotation.overrideEverythingButGameStatus(getCastlingString());
    }

    public Castling(Castling move) {
        super(move);
        side = move.side;
    }

    public String getCastlingString() {
        return CASTLING_ANNOTATION[side];
    }

    public int getSide() {
        return side;
    }

    public boolean isKingSide() {
        return side == CastlingAbility.KING_SIDE;
    }

}
