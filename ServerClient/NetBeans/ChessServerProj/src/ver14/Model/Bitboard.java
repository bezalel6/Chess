package ver14.Model;

import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.moves.Direction;

import java.io.Serializable;
import java.util.Objects;

public class Bitboard implements Serializable {

    private long bitBoard;
    private long lastSet;
    private LocsList setLocs = null;
    private VoidCallback onSet;
    private Location lastSetLoc;

    /**
     * creates an empty bitboard and sets loc to true
     *
     * @param loc
     */
    public Bitboard(Location loc) {
        this();
        set(loc, true);
    }

    public Bitboard() {
        this(0L);
    }

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

    public Bitboard(long bitBoard) {
        this.bitBoard = bitBoard;
    }

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
        this.setLocs = other.setLocs;
    }

    public Bitboard cp() {
        return new Bitboard(this);
    }

    public boolean notEmpty() {
        return this.bitBoard != 0;
    }

    public Bitboard orEqual(Bitboard other) {
        if (other != null) {
            return orEqual(other.bitBoard);
        }
        return this;
    }

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

            bitBoard &= currentDirection.andWith;
        }

        return this;
    }


    public Bitboard orEqual(long l) {

        bitBoard |= l;
        return this;
    }

    public void setOnSet(VoidCallback onSet) {
        this.onSet = onSet;
    }

    public boolean isEmpty() {
        return bitBoard == 0L;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitBoard);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bitboard bitboard = (Bitboard) o;
        return bitBoard == bitboard.bitBoard;
    }

    @Override
    public String toString() {
        return Long.toBinaryString(bitBoard);
    }

    public Bitboard shift(PlayerColor playerColor, Direction direction) {
        return cp().shiftMe(playerColor, direction);
    }

    public Location getLastSetLoc() {
        return lastSetLoc;
    }

    public LocsList getSetLocs() {
        if (lastSet != bitBoard || setLocs == null)
            setSetLocs();
        return setLocs;
    }

    private void setSetLocs() {
        lastSet = bitBoard;
        setLocs = new LocsList();
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

    public Bitboard orEqual(Location loc) {
        return orEqual(loc.asLong);
    }

    public long getBitBoard() {
        return bitBoard;
    }

    public void reset() {

        bitBoard = 0L;
    }

    /**
     * CHANGES SELF
     *
     * @param other
     * @return MY CHANGED SELF
     */
    public Bitboard exclude(Bitboard other) {
        this.bitBoard &= ~other.bitBoard;

        return this;
    }

    public void prettyPrint() {
        prettyPrint("");
    }

    public void prettyPrint(String desc) {
        System.out.println();
        System.out.println(prettyBoard(desc));

    }

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

    public boolean isSet(Location loc) {
        return (bitBoard & loc.asLong) != 0;
    }

    public String prettyBoard() {
        return prettyBoard("");

    }

    public boolean anyMatch(Bitboard other) {
        return anyMatch(other.bitBoard);
    }

    public boolean anyMatch(long otherBB) {
        return (bitBoard & otherBB) != 0;
    }

    public boolean anyMatch(Location loc) {
        return anyMatch(loc.asLong);
    }

    public Bitboard and(Bitboard other) {
        return and(other.bitBoard);
    }

    public Bitboard and(long other) {
        return cp().andEqual(other);
    }

    /**
     * CHANGES SELF
     *
     * @param l
     * @return myself
     */
    public synchronized Bitboard andEqual(long l) {
        bitBoard &= l;
        lastSetLoc = null;
        return this;
    }

    private synchronized Bitboard andEqual(Bitboard other) {
        return andEqual(other.bitBoard);
    }
}
