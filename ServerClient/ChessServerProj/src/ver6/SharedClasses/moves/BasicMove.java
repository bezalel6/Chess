package ver6.SharedClasses.moves;


import ver6.SharedClasses.Location;

import java.io.Serializable;
import java.util.Objects;

public class BasicMove implements Serializable {
    Location movingFrom;
    Location movingTo;

    public BasicMove(BasicMove other) {
        this(other.movingFrom, other.movingTo);
    }

    public BasicMove(Location movingFrom, Location movingTo) {
        this.movingFrom = movingFrom;
        this.movingTo = movingTo;
    }

    public BasicMove(String move) {
        this(Location.getLoc(move.substring(0, 2)), Location.getLoc(move.substring(2, 4)));
    }

    public static BasicMove getFlipped(BasicMove basicMove) {
        BasicMove ret = new BasicMove(basicMove);
        ret.flip();
        return ret;

    }

    public void flip() {
        Location t = movingFrom;
        movingFrom = movingTo;
        movingTo = t;
    }

    public Location getMovingFrom() {
        return movingFrom;
    }

    public void setMovingFrom(Location movingFrom) {
        this.movingFrom = movingFrom;
    }

    public Location getMovingTo() {
        return movingTo;
    }

    public void setMovingTo(Location movingTo) {
        this.movingTo = movingTo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(movingFrom, movingTo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicMove basicMove)) return false;
        return Objects.equals(movingFrom, basicMove.movingFrom) && Objects.equals(movingTo, basicMove.movingTo);
    }

    @Override
    public String toString() {
        return getBasicMoveString();
    }

    public String getBasicMoveString() {
        return movingFrom.toString() + movingTo.toString();
    }
}
