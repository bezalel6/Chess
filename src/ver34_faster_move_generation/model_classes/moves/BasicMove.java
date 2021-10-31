package ver34_faster_move_generation.model_classes.moves;

import ver34_faster_move_generation.Location;

public class BasicMove {
    final Location movingFrom;
    final Location movingTo;

    public BasicMove(Location movingFrom, Location movingTo) {
        this.movingFrom = new Location(movingFrom);
        this.movingTo = new Location(movingTo);
    }

    public BasicMove(BasicMove other) {
        this(other.movingFrom, other.movingTo);
    }

    public Location getMovingFrom() {
        return movingFrom;
    }

    public Location getMovingTo() {
        return movingTo;
    }

    public BasicMove flip() {
        return new BasicMove(movingTo, movingFrom);
    }

    @Override
    public String toString() {
        return movingFrom.toString() + movingTo.toString();
    }
}
