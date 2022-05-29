package ver14.SharedClasses.Game.Moves;


import ver14.SharedClasses.Game.Location;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;


/**
 * Basic move - represents a basic move. with a source and a destination.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class BasicMove implements Serializable {
    @Serial
    private static final long serialVersionUID = 42069_000_000L;
    /**
     * The source.
     */
    Location source;
    /**
     * The destination.
     */
    Location destination;


    /**
     * Copy constructor.
     *
     * @param other the other
     */
    public BasicMove(BasicMove other) {
        this(other.source, other.destination);
    }

    /**
     * Instantiates a new Basic move.
     *
     * @param source      the source
     * @param destination the destination
     */
    public BasicMove(Location source, Location destination) {
        this.source = source;
        this.destination = destination;
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
     * Gets flipped.
     *
     * @return the flipped
     */
    public BasicMove getFlipped() {
        return getFlipped(this);
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
        Location t = source;
        source = destination;
        destination = t;
    }

    /**
     * Gets source. aka source
     *
     * @return the source
     */
    public Location getSource() {
        return source;
    }

    /**
     * Sets source.
     *
     * @param source the source
     */
    public void setSource(Location source) {
        this.source = source;
    }

    /**
     * Gets destination.
     *
     * @return the destination
     */
    public Location getDestination() {
        return destination;
    }

    /**
     * Sets destination.
     *
     * @param destination the destination
     */
    public void setDestination(Location destination) {
        this.destination = destination;
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(source, destination);
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
        return Objects.equals(source, basicMove.source) && Objects.equals(destination, basicMove.destination);
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
