package ver4.model_classes;

import ver4.SharedClasses.Location;
import ver4.SharedClasses.PlayerColor;

import java.io.Serializable;
import java.util.ArrayList;

public class Bitboard implements Serializable {
    public static final long notAFile = 0xfefefefefefefefeL;
    public static final long notHFile = 0x7f7f7f7f7f7f7f7fL;
    private static final long[] preCalc = new long[Location.NUM_OF_SQUARES];

    static {
        for (int i = 0; i < Location.NUM_OF_SQUARES; i++) {
            preCalc[i] = 1L << i;
        }
    }

    private long bitBoard;
    private ArrayList<Location> setLocs = null;

    public Bitboard() {
        this(0L);
    }

    public Bitboard(Bitboard other) {
        this(other.bitBoard);
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

    private static long getLong(Location loc) {
        return getLong(loc.asInt());
    }

    private static long getLong(int loc) {
        return preCalc[loc];
    }

    public Bitboard shift(int shiftBy) {
        if (shiftBy > 0) {
            return new Bitboard(bitBoard >>> shiftBy);
        }

        return new Bitboard(bitBoard << (shiftBy * -1));
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

    public ArrayList<Location> getSetLocs() {
        if (setLocs == null)
            setSetLocs();
        return setLocs;
    }

    private void setSetLocs() {
        setLocs = new ArrayList<>();
//        int position = 1;
//        long num = bitBoard;
//        while (num != 0) {
//            if ((num & 1) != 0) {
//                Location loc = Location.getLoc(position - 1);
//                assert loc != null;
//                setLocs.add(loc);
//            }
//            position++;
//            num = num >>> 1;
//        }
        for (Location loc : Location.ALL_LOCS) {
            if (isSet(loc))
                setLocs.add(loc);
        }
    }

    public boolean isSet(Location loc) {
        return (bitBoard & getLong(loc)) != 0;
    }

    public void set(Location loc, boolean state) {
        setLocs = null;

        long l = getLong(loc);
        if (state) {
            bitBoard |= l;
        } else {
            bitBoard &= ~(l);
        }
    }

    public Bitboard orEqual(Bitboard other) {
        if (other == null) {
            return this;
        }
        setLocs = null;

        orEqual(other.bitBoard);
        return this;
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

    public boolean isEmpty() {
        return bitBoard == 0L;
    }

    //region shift by one
    public Bitboard shiftUp() {
        return shift(8);
    }

    public Bitboard shiftDown() {
        return shift(-8);
    }

    public Bitboard shiftLeftUp(PlayerColor clr) {
        int mult = clr == PlayerColor.WHITE ? 1 : -1;
        long and = clr == PlayerColor.WHITE ? notAFile : notHFile;
        return shift(7 * mult).andEqual(and);
    }

    public Bitboard shiftRightUp(PlayerColor clr) {
        int mult = clr == PlayerColor.WHITE ? 1 : -1;

        long and = clr == PlayerColor.WHITE ? notHFile : notAFile;

        return shift(9 * mult).andEqual(and);
    }

    public Bitboard shiftLeftDown(PlayerColor clr) {
        return shiftLeftUp(clr.getOpponent());
    }

    public Bitboard shiftRightDown(PlayerColor clr) {
        return shiftRightUp(clr.getOpponent());
    }

    public Bitboard shiftLeft() {
        return shift(-1).andEqual(notAFile);
    }
    //endregion

    public Bitboard shiftRight() {
        return shift(1).andEqual(notHFile);
    }

    /**
     * CHANGES SELF
     *
     * @param other
     * @return MY CHANGED SELF
     */
    public Bitboard xor(Bitboard other) {
        this.bitBoard &= ~other.bitBoard;
        setLocs = null;

        return this;
    }

    public void prettyPrint() {
        System.out.println();
        System.out.println(prettyBoard());
    }
}
