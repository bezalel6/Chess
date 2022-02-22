package ver3.view.moves;

import ver3.view.Location;

import java.util.Objects;

public class BasicMove {
    Location movingFrom;
    Location movingTo;

    public BasicMove(Location movingFrom, Location movingTo) {
        this.movingFrom = movingFrom;
        this.movingTo = movingTo;
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
        Location t = movingFrom;
        movingFrom = movingTo;
        movingTo = t;
    }

    public String getBasicMoveString() {
        return movingFrom.toString() + movingTo.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicMove basicMove = (BasicMove) o;
        return Objects.equals(movingFrom, basicMove.movingFrom) && Objects.equals(movingTo, basicMove.movingTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movingFrom, movingTo);
    }

    @Override
    public String toString() {
        return getBasicMoveString();
    }
}
