package ver13.Model;

import ver13.SharedClasses.Callbacks.VoidCallback;
import ver13.SharedClasses.Location;
import ver13.SharedClasses.PlayerColor;
import ver13.SharedClasses.moves.Direction;

import java.io.Serializable;
import java.util.Objects;

public class Bitboard implements Serializable {
    private static final long[] preCalc = new long[Location.NUM_OF_SQUARES];
//    private static final MyHashMap<LocsList> setLocsHashMap = new MyHashMap<>(HashManager.Size.BB_SET_LOCS);

    static {
        for (int i = 0; i < Location.NUM_OF_SQUARES; i++) {
            preCalc[i] = 1L << i;
        }
    }

    private long bitBoard;
    private long lastSet;
    private LocsList setLocs = null;
    private VoidCallback onSet;

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
        long l = loc.asLong;
        if (state) {
            bitBoard |= l;
//            if (this.setLocs != null) {
//                this.setLocs.add(loc);
//                lastSet = bitBoard;
//            }
        } else {
            bitBoard &= ~(l);
//            if (this.setLocs != null) {
//                this.setLocs.remove(loc);
//                lastSet = bitBoard;
//            }
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
        Direction[] combination = direction.combination;
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
        return new Bitboard().shiftMe(playerColor, direction);
    }

    public LocsList getSetLocs() {
        if (lastSet != bitBoard || setLocs == null)
            setSetLocs();
        return setLocs;
    }

    private void setSetLocs() {
        lastSet = bitBoard;
//        if (setLocsHashMap.containsKey(bitBoard)) {
//            setLocs = setLocsHashMap.get(bitBoard);
//            return;
//        }
        setLocs = new LocsList();
        int position = 1;
        long num = bitBoard;
        while (num != 0) {
            if ((num & 1) != 0) {
                Location loc = Location.getLoc(position - 1);
                setLocs.add(loc);
            }
            position++;
            num = num >>> 1;
        }
//        setLocsHashMap.put(bitBoard, setLocs);
//        for (Location loc : Location.ALL_LOCS) {
//            if (isSet(loc))
//                setLocs.add(loc);
//        }
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
        System.out.println();
        System.out.println(prettyBoard());

    }

    public String prettyBoard() {
        StringBuilder stringBuilder = new StringBuilder();
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
    public Bitboard andEqual(long l) {
        bitBoard &= l;

        return this;
    }

    private Bitboard andEqual(Bitboard other) {
        return andEqual(other.bitBoard);
    }
}
