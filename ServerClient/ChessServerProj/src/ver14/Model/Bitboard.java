package ver14.Model;

import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.Moves.BitData;
import ver14.SharedClasses.Game.Moves.Direction;
import ver14.SharedClasses.Game.PlayerColor;

import java.io.Serializable;
import java.util.Objects;


/**
 * Bitboard - a bitboard representation of the chess board. every bit set is a chess piece on that square.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 * @see <a href="www.chessprogramming.org/Bitboards">Bitboards</a>
 */
public class Bitboard implements Serializable {

    private long bitBoard;
    private long lastSet;
    private Locs setLocs = null;
    private VoidCallback onSet;
    private Location lastSetLoc;

    /**
     * creates an empty bitboard and sets loc to true
     *
     * @param loc the location to set
     */
    public Bitboard(Location loc) {
        this();
        set(loc, true);
    }

    /**
     * Instantiates a new empty Bitboard.
     */
    public Bitboard() {
        this(0L);
    }

    /**
     * Sets the bit on the loc according to state. if state is true, the bit is set to 1 if state is false the bit is set to 0
     *
     * @param loc   the loc
     * @param state the state.
     */
    public void set(Location loc, boolean state) {
        if (state) {
            bitBoard |= loc.asLong;
            lastSetLoc = loc;
        } else {
            bitBoard &= ~loc.asLong;
            lastSetLoc = null;
        }
        if (onSet != null)
            onSet.callback();

    }

    /**
     * Instantiates a new Bitboard.
     *
     * @param bitBoard the bit board
     */
    public Bitboard(long bitBoard) {
        this.bitBoard = bitBoard;
    }

    /**
     * Instantiates a new Bitboard. and filling it in the direction
     *
     * @param start       the start
     * @param end         the end
     * @param direction   the direction
     * @param playerColor the player color
     */
    public Bitboard(Bitboard start, Bitboard end, Direction direction, PlayerColor playerColor) {
        this(start);
        Bitboard filling = start.cp();
        while (filling.notEmpty() && !filling.equals(end)) {
            orEqual(filling);
            filling.shiftMe(playerColor, direction);
        }
    }

    private Bitboard(Bitboard other) {
        this(other.bitBoard);
        if (other.setLocs != null)
            this.setLocs = new Locs(other.setLocs);
    }

    /**
     * creates a copy of this bitboard.
     *
     * @return the copied bitboard
     */
    public Bitboard cp() {
        return new Bitboard(this);
    }

    /**
     * if the board is not empty
     *
     * @return the boolean
     */
    public boolean notEmpty() {
        return this.bitBoard != 0;
    }

    /**
     * Or equal bitboard.
     * performs a bitwise or on this board
     *
     * @param other the other
     * @return the bitboard
     */
    public Bitboard orEqual(Bitboard other) {
        if (other != null) {
            return orEqual(other.bitBoard);
        }
        return this;
    }

    /**
     * performs a bitwise shift on this bitboard instance.
     *
     * @param playerColor the player color. used for perspective
     * @param direction   the direction to shift in
     * @return the changed bitboard. used to chain actions
     */
    public Bitboard shiftMe(PlayerColor playerColor, Direction direction) {
        Direction[] combination = direction.getCombination();
        for (int i = 0, combinationLength = combination.length; i < combinationLength; i++) {
            Direction currentDirection = combination[i];
            currentDirection = currentDirection.perspective(playerColor);
            int shiftBy = currentDirection.offset;

            if (shiftBy > 0)
                bitBoard = bitBoard >>> shiftBy;
            else
                bitBoard = bitBoard << (shiftBy * -1);
            if (currentDirection.andWith != BitData.everything)
                bitBoard &= currentDirection.andWith;
        }

        return this;
    }


    /**
     * Or equal bitboard.
     * performs a bitwise or on this board
     *
     * @param l the other board to or with
     * @return the changed bitboard
     */
    public Bitboard orEqual(long l) {

        bitBoard |= l;
        return this;
    }

    /**
     * Sets the callback on change of any bit in this bitboard
     *
     * @param onSet the on set
     */
    public void setOnSet(VoidCallback onSet) {
        this.onSet = onSet;
    }

    /**
     * Is this board empty
     *
     * @return is this board empty
     */
    public boolean isEmpty() {
        return bitBoard == 0L;
    }

    /**
     * Hash code
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(bitBoard);
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
        if (o == null || getClass() != o.getClass()) return false;
        Bitboard bitboard = (Bitboard) o;
        return bitBoard == bitboard.bitBoard;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return Long.toBinaryString(bitBoard);
    }

    /**
     * Shift bitboard.
     *
     * @param playerColor the player color
     * @param direction   the direction
     * @return the bitboard
     */
    public Bitboard shift(PlayerColor playerColor, Direction direction) {
        return cp().shiftMe(playerColor, direction);
    }

    /**
     * Gets the location last set to true
     *
     * @return the last set loc
     */
    public Location getLastSetLoc() {
        return lastSetLoc;
    }

    /**
     * Gets all the locations set to true
     *
     * @return the set locs
     */
    public Locs getSetLocs() {
        if (lastSet != bitBoard || setLocs == null)
            setSetLocs();
        return setLocs;
    }

    /**
     * calculates all the set locs
     */
    private void setSetLocs() {
        lastSet = bitBoard;
        setLocs = new Locs();
        int position = 1;
        long num = bitBoard;
        while (num != 0) {
            if ((num & 1) != 0) {
                Location loc = Location.getLoc(position - 1);
                setLocs.add(loc);
                lastSetLoc = loc;
            }
            position++;
            num = num >>> 1;
        }
    }

    /**
     * Or equal bitboard.
     *
     * @param loc the loc
     * @return the bitboard
     */
    public Bitboard orEqual(Location loc) {
        return orEqual(loc.asLong);
    }

    /**
     * Gets bit board.
     *
     * @return the bit board
     */
    public long getBitBoard() {
        return bitBoard;
    }

    /**
     * Resets the bitboard to zero
     */
    public void reset() {

        bitBoard = 0L;
    }

    /**
     * performs a bitwise and with 2's compliment of other. practically setting all other bits to zero
     *
     * @param other the other
     * @return MY CHANGED SELF
     */
    public Bitboard exclude(Bitboard other) {
        this.bitBoard &= ~other.bitBoard;

        return this;
    }

    /**
     * Pretty print.
     */
    public void prettyPrint() {
        prettyPrint("");
    }

    /**
     * Pretty print.
     *
     * @param desc the desc
     */
    public void prettyPrint(String desc) {
        System.out.println();
        System.out.println(prettyBoard(desc));

    }

    /**
     * Pretty board string.
     *
     * @param desc the desc
     * @return the string
     */
    public String prettyBoard(String desc) {
        StringBuilder stringBuilder = new StringBuilder(desc + "\n");
        String RESET = "\033[0m";
        String RED = "\033[0;31m";
        for (int i = 0; i < Location.NUM_OF_SQUARES; i++) {
            stringBuilder.append("|");
            String s = "0";
            if (isSet(Location.getLoc(i))) {
                s = RED + "1" + RESET;
            }
            stringBuilder.append(s).append("| ");
            if ((i + 1) % 8 == 0)
                stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Is a location set.
     *
     * @param loc the loc
     * @return the boolean
     */
    public boolean isSet(Location loc) {
        return (bitBoard & loc.asLong) != 0;
    }

    /**
     * Pretty board string.
     *
     * @return the string
     */
    public String prettyBoard() {
        return prettyBoard("");

    }

    /**
     * Any match with the other board.
     *
     * @param other the other
     * @return the boolean
     */
    public boolean anyMatch(Bitboard other) {
        return anyMatch(other.bitBoard);
    }

    /**
     * Any match with the other board.
     *
     * @param otherBB the other bb
     * @return the boolean
     */
    public boolean anyMatch(long otherBB) {
        return (bitBoard & otherBB) != 0;
    }

    /**
     * performs a bitwise and on a new copy of this bitboard.
     *
     * @param other the other
     * @return the new changed bitboard
     */
    public Bitboard and(Bitboard other) {
        return and(other.bitBoard);
    }

    /**
     * performes a bitwise and on a new copy of this bitboard.
     *
     * @param other the other
     * @return the new changed bitboard
     */
    public Bitboard and(long other) {
        return cp().andEqual(other);
    }

    /**
     * performs a bitwise and on this board and l. also invalidates set locs which might be expensive
     *
     * @param l the l
     * @return myself bitboard
     */
    public Bitboard andEqual(long l) {
        bitBoard &= l;
        lastSetLoc = null;
        return this;
    }

}
