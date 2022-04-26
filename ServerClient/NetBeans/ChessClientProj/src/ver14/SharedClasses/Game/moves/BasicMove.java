package ver14.SharedClasses.Game.Moves;


import ver14.SharedClasses.Game.Location;

import java.io.Serializable;
import java.util.Objects;


/**
 * Basic move - represents a basic move. with a source and a destination.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class BasicMove implements Serializable {
    /**
     * The Moving from.
     */
    Location movingFrom;
    /**
     * The Moving to.
     */
    Location movingTo;


    /**
     * Copy constructor.
     *
     * @param other the other
     */
    public BasicMove(BasicMove other) {
        this(other.movingFrom, other.movingTo);
    }

    /**
     * Instantiates a new Basic move.
     *
     * @param movingFrom the moving from
     * @param movingTo   the moving to
     */
    public BasicMove(Location movingFrom, Location movingTo) {
        this.movingFrom = movingFrom;
        this.movingTo = movingTo;
    }

    /**
     * Instantiates a new Basic move.
     *
     * @param move the move
     */
    public BasicMove(String move) {
        this(Location.getLoc(move.substring(0, 2)), Location.getLoc(move.substring(2, 4)));
    }

    /**
     * Create batch basic move [ ].
     *
     * @param locs the locs
     * @return the basic move [ ]
     */
    public static BasicMove[] createBatch(Location... locs) {
        BasicMove[] ret = new BasicMove[locs.length / 2];
        for (int i = 0, j = 0; i < locs.length - 1; i += 2, j++) {
            ret[j] = new BasicMove(locs[i], locs[i + 1]);
        }
        return ret;
    }

    /**
     * Gets a copy of the provided move with the source and destination flipped
     *
     * @param basicMove the original move
     * @return the flipped move
     */
    public static BasicMove getFlipped(BasicMove basicMove) {
        BasicMove ret = new BasicMove(basicMove);
        ret.flip();
        return ret;
    }

    /**
     * Flips the source and destination.
     */
    public void flip() {
        Location t = movingFrom;
        movingFrom = movingTo;
        movingTo = t;
    }

    /**
     * Gets moving from. aka source
     *
     * @return the moving from
     */
    public Location getMovingFrom() {
        return movingFrom;
    }

    /**
     * Sets moving from.
     *
     * @param movingFrom the moving from
     */
    public void setMovingFrom(Location movingFrom) {
        this.movingFrom = movingFrom;
    }

    /**
     * Gets moving to.
     *
     * @return the moving to
     */
    public Location getMovingTo() {
        return movingTo;
    }

    /**
     * Sets moving to.
     *
     * @param movingTo the moving to
     */
    public void setMovingTo(Location movingTo) {
        this.movingTo = movingTo;
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(movingFrom, movingTo);
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicMove basicMove)) return false;
        return Objects.equals(movingFrom, basicMove.movingFrom) && Objects.equals(movingTo, basicMove.movingTo);
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return getBasicMoveAnnotation();
    }

    /**
     * Gets basic move annotation.
     *
     * @return the basic move annotation
     */
    public String getBasicMoveAnnotation() {
        return MoveAnnotation.basicAnnotate(this);
    }

    /**
     * copies this move.
     *
     * @return the new copy
     */
    public BasicMove cp() {
        return new BasicMove(this);
    }
}
