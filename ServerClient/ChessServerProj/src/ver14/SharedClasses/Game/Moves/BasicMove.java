package ver14.SharedClasses.Game.Moves;


import ver14.SharedClasses.Game.Location;

import java.io.Serializable;
import java.util.Objects;

/*
 * BasicMove
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * BasicMove -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * BasicMove -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

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

    public static BasicMove[] createBatch(Location... locs) {
        BasicMove[] ret = new BasicMove[locs.length / 2];
        for (int i = 0, j = 0; i < locs.length - 1; i += 2, j++) {
            ret[j] = new BasicMove(locs[i], locs[i + 1]);
        }
        return ret;
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
        return getBasicMoveAnnotation();
    }

    public String getBasicMoveAnnotation() {
        return MoveAnnotation.basicAnnotate(this);
    }

    public BasicMove cp() {
        return new BasicMove(this);
    }
}