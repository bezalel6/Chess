package ver34_faster_move_generation.model_classes.moves;

import ver34_faster_move_generation.Location;

public class BasicMove {
    Location movingFrom;
    Location movingTo;

    public BasicMove(Location movingFrom, Location movingTo) {
        this.movingFrom = new Location(movingFrom);
        this.movingTo = new Location(movingTo);
    }

    public BasicMove(BasicMove other) {
        this(other.movingFrom, other.movingTo);
    }

    public static BasicMove getFlipped(BasicMove basicMove) {
        BasicMove ret = new BasicMove(basicMove);
        ret.flip();
        return ret;

    }

    public Location getMovingFrom() {
        return movingFrom;
    }

    public Location getMovingTo() {
        return movingTo;
    }

    public void flip() {
        Location t = new Location(movingFrom);
        movingFrom = new Location(movingTo);
        movingTo = t;
    }

    @Override
    public String toString() {
        return movingFrom.toString() + movingTo.toString();
    }
}
