package ver36_no_more_location.model_classes;

import ver36_no_more_location.Location;

public class Bitboard {
    private long bitBoard;

    public Bitboard() {
        this(0L);
    }

    public Bitboard(long bitBoard) {
        this.bitBoard = bitBoard;
    }

    public void set(Location loc, boolean state) {
        if (state) {
            bitBoard |= 1L << loc.ordinal();
        } else {
            bitBoard &= ~(1L << loc.ordinal());           // shorthand
        }
    }

    public long getBitBoard() {
        return bitBoard;
    }

}
