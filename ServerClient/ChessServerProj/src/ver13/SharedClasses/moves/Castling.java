package ver13.SharedClasses.moves;

import ver13.SharedClasses.Location;

public class Castling extends Move {
    private final CastlingRights.Side side;
    private final Location rookOrigin;

    public Castling(Location movingFrom, Location movingTo, CastlingRights.Side side) {
        super(movingFrom, movingTo);
        this.side = side;
        rookOrigin = Location.getLoc(movingFrom.row, side.rookStartingCol);
        Location rookDestination = Location.getLoc(movingFrom.row, side.castledRookCol);
        assert rookOrigin != null && rookDestination != null;
        intermediateMove = new BasicMove(rookOrigin, rookDestination);
//        moveAnnotation.overrideEverythingButGameStatus(getCastlingString());
    }

    public Castling(Castling move) {
        super(move);
        side = move.side;
        this.rookOrigin = move.rookOrigin;
    }

    public String getCastlingString() {
        return side.castlingNotation;
    }

    public Location getRookOrigin() {
        return rookOrigin;
    }

    public CastlingRights.Side getSide() {
        return side;
    }

    public boolean isKingSide() {
        return side == CastlingRights.Side.KING;
    }

}
