package ver21_square_class.model_classes;

import ver21_square_class.Error;

import java.util.Arrays;


public class CastlingAbility {
    public static final String NO_CASTLING_ABILITY = "-";
    private static final char[] NAMES_LOOKUP = new char[]{'K', 'Q', 'k', 'q'};
    private static final int allFalseHash = Arrays.hashCode(new boolean[4]);

    private final boolean[] castlingAbility;

    public CastlingAbility(CastlingAbility other) {
        castlingAbility = Arrays.copyOf(other.castlingAbility, other.castlingAbility.length);
    }

    public CastlingAbility(String str) {
        castlingAbility = new boolean[4];

        if (str.equals("-")) {
            return;
        }
        char[] arr = str.toCharArray();
        for (char c : arr) {
            castlingAbility[indexOf(c)] = true;
        }
    }

    private static int indexOf(char c) {
        for (int i = 0, arrLength = CastlingAbility.NAMES_LOOKUP.length; i < arrLength; i++) {
            char t = CastlingAbility.NAMES_LOOKUP[i];
            if (t == c)
                return i;
        }
        Error.error("idk index of or smn");
        return -1;
    }

    boolean getCastlingAbility(int player, int side) {
        return castlingAbility[player * 2 + side];
    }

    /**
     * manually set ability by side
     *
     * @param player
     * @param side
     * @param b
     */
    public void setCastlingAbility(int player, int side, boolean b) {
        castlingAbility[player * 2 + side] = b;
    }

    /**
     * set ability to false by side
     *
     * @param player
     * @param side
     */
    public void setCastlingAbility(int player, int side) {
        castlingAbility[player * 2 + side] = false;
    }

    /**
     * set all player's ability to false
     *
     * @param player
     */
    public void setCastlingAbility(int player) {
        player *= 2;
        Arrays.fill(castlingAbility, player, player + 2, false);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        if (Arrays.hashCode(castlingAbility) == allFalseHash)
            return NO_CASTLING_ABILITY;
        for (int i = 0; i < castlingAbility.length; i++) {
            if (castlingAbility[i]) {
                ret.append(NAMES_LOOKUP[i]);
            }
        }
        return ret.toString();
    }

    public boolean[] getCastlingAbility(int player) {
        player *= 2;
        return Arrays.copyOfRange(castlingAbility, player, player + 2);
    }
}