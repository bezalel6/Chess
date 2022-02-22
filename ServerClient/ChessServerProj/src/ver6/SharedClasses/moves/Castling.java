package ver6.SharedClasses.moves;

import ver6.SharedClasses.Location;

public class Castling extends Move {
    private final CastlingRights.Side side;
    private final Location rookOrigin;

    public Castling(Location movingFrom, Location movingTo, CastlingRights.Side side) {
        super(movingFrom, movingTo);
        this.side = side;
        rookOrigin = Location.getLoc(movingFrom.getRow(), side.rookStartingCol);
        Location rookDestination = Location.getLoc(movingFrom.getRow(), side.castledRookCol);
        assert rookOrigin != null && rookDestination != null;
        intermediateMove = new BasicMove(rookOrigin, rookDestination);
        moveAnnotation.overrideEverythingButGameStatus(getCastlingString());
    }

    public String getCastlingString() {
        return side.castlingNotation;
    }

    public Castling(Castling move) {
        super(move);
        side = move.side;
        this.rookOrigin = move.rookOrigin;
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
