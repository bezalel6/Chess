package ver9.model_classes;

import ver9.SharedClasses.Hashable;
import ver9.SharedClasses.Location;
import ver9.SharedClasses.PlayerColor;
import ver9.SharedClasses.moves.Direction;
import ver9.model_classes.hashing.HashManager;
import ver9.model_classes.hashing.my_hash_maps.MyHashMap;

import java.io.Serializable;
import java.util.Objects;

public class Bitboard implements Serializable, Hashable {

    private static final long[] preCalc = new long[Location.NUM_OF_SQUARES];
    private static final MyHashMap setLocsHashMap = new MyHashMap(HashManager.Size.BB_SET_LOCS);

    static {
        for (int i = 0; i < Location.NUM_OF_SQUARES; i++) {
            preCalc[i] = 1L << i;
        }
    }

    private long bitBoard;
    private LocsList setLocs = null;

    public Bitboard() {
        this(0L);
    }

    private Bitboard(Bitboard other) {
        this(other.bitBoard);
        setLocs = other.setLocs == null ? null : new LocsList(other.setLocs);
    }

    public Bitboard(long bitBoard) {
        this.bitBoard = bitBoard;
    }

    /**
     * creates an empty bitboard and sets loc to true
     *
     * @param loc
     */
    public Bitboard(Location loc) {
        this();
        set(loc, true);
    }

    public Bitboard(Bitboard start, Bitboard end, Direction direction, PlayerColor playerColor) {
        this(start);
        Bitboard filling = start.cp();
        while (filling.notEmpty() && !filling.equals(end)) {
            orEqual(filling);
            filling.shiftMe(playerColor, direction);
        }
    }

    public static long getLong(Location loc) {
        return getLong(loc.asInt());
    }

    public static long getLong(int loc) {
        return 1L << loc;

//        return preCalc[loc];
    }

    public Bitboard cp() {
        return new Bitboard(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bitboard bitboard = (Bitboard) o;
        return bitBoard == bitboard.bitBoard;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitBoard);
    }

    public Bitboard shift(PlayerColor playerColor, Direction direction) {
        return new Bitboard().shiftMe(playerColor, direction);
    }

    public Bitboard shiftMe(PlayerColor playerColor, Direction direction) {
        for (Direction currentDirection : direction.combination) {
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

    /**
     * CHANGES SELF
     *
     * @param l
     * @return myself
     */
    public Bitboard andEqual(long l) {
        setLocs = null;
        bitBoard &= l;
        return this;
    }

    public LocsList getSetLocs() {
        if (setLocs == null)
            setSetLocs();
        return setLocs;
    }

    private void setSetLocs() {
        if (setLocsHashMap.containsKey(bitBoard)) {
            setLocs = (LocsList) setLocsHashMap.get(bitBoard);
        }
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
        setLocsHashMap.put(bitBoard, setLocs);
//        for (Location loc : Location.ALL_LOCS) {
//            if (isSet(loc))
//                setLocs.add(loc);
//        }
    }

    public boolean isSet(Location loc) {
        return (bitBoard & getLong(loc)) != 0;
    }

    public void set(Location loc, boolean state) {
        setLocs = null;

        long l = getLong(loc);
        if (state) {
//            if (setLocs != null && !setLocs.contains(loc)) {
//                setLocs.add(loc);
//            }
            bitBoard |= l;
        } else {
//            if (setLocs != null)
//                setLocs.remove(loc);
            bitBoard &= ~(l);
        }
    }

    public Bitboard orEqual(Bitboard other) {
        if (other == null) {
            return this;
        }
        orEqual(other.bitBoard);
        return this;
    }

    public Bitboard orEqual(Location loc) {
        return orEqual(getLong(loc));
    }

    public Bitboard orEqual(long l) {
        setLocs = null;

        bitBoard |= l;
        return this;
    }

    public long getBitBoard() {
        return bitBoard;
    }

    @Override
    public String toString() {
        return Long.toBinaryString(bitBoard);
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

    public void reset() {
        bitBoard = 0L;
    }

    public boolean notEmpty() {
        return !isEmpty();
    }

    public boolean isEmpty() {
        return bitBoard == 0L;
    }

    /**
     * CHANGES SELF
     *
     * @param other
     * @return MY CHANGED SELF
     */
    public Bitboard exclude(Bitboard other) {
        this.bitBoard &= ~other.bitBoard;
        setLocs = null;

        return this;
    }

    public void prettyPrint() {
        System.out.println();
        System.out.println(prettyBoard());

    }

    public boolean anyMatch(Bitboard other) {
        return anyMatch(other.bitBoard);
    }

    public boolean anyMatch(Location loc) {
        return anyMatch(getLong(loc));
    }

    public boolean anyMatch(long otherBB) {
        return (bitBoard & otherBB) != 0;
    }

    public Bitboard and(Bitboard other) {
        return and(other.bitBoard);
    }

    public Bitboard and(long other) {
        return cp().andEqual(other);
    }

    private Bitboard andEqual(Bitboard other) {
        return andEqual(other.bitBoard);
    }
}
