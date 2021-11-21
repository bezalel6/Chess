package ver36_no_more_location.model_classes;

import ver36_no_more_location.Location;

import java.util.ArrayList;

public class Bitboard {
    public static final Bitboard avoidWrap = new Bitboard();
    private long bitBoard;

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

    public Bitboard shift(int shiftBy, boolean shiftRight) {
        if (shiftRight) {
            return new Bitboard(bitBoard >>> shiftBy);
        }
        return new Bitboard(bitBoard << shiftBy);
    }

    public ArrayList<Location> getSetLocs() {
        ArrayList<Location> positions = new ArrayList<>();
        int position = 1;
        long num = bitBoard;
        while (num != 0) {
            if ((num & 1) != 0) {
                Location loc = Location.getLoc(position - 1);
                assert loc != null;
                positions.add(loc);
            }
            position++;
            num = num >>> 1;
        }
        return positions;
    }

    public long or(Bitboard other) {
        return bitBoard | other.bitBoard;
    }

    public void orEqual(Bitboard other) {
        orEqual(other.bitBoard);
    }

    public void orEqual(long l) {
        bitBoard |= l;
    }

    //todo precalc every possible loc and & with it
    public boolean isSet(Location loc) {
        return ((bitBoard >> loc.asInt()) & 1) == 1;
    }

    public void set(Location loc, boolean state) {
        if (state) {
            bitBoard |= 1L << loc.asInt();
        } else {
            bitBoard &= ~(1L << loc.asInt());
        }
    }

    public long getBitBoard() {
        return bitBoard;
    }

    @Override
    public String toString() {
        return Long.toBinaryString(bitBoard);
    }

    public void reset() {
        bitBoard = 0L;
    }

    public boolean isEmpty() {
        return bitBoard == 0L;
    }
}
