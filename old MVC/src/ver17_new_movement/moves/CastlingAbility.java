package ver17_new_movement.moves;

import java.util.Arrays;
import java.util.stream.IntStream;


public class CastlingAbility {
    public static final String NO_CASTLING_ABILITY = "-";
    private static final char[] NAMES_LOOKUP = new char[]{'K', 'Q', 'k', 'q'};
    private int allFalseHash;
    private boolean[] castlingAbility;

    public CastlingAbility(String str) {
        castlingAbility = new boolean[4];
        allFalseHash = Arrays.hashCode(castlingAbility);
        if (str.equals("-")) {
            return;
        }
        char arr[] = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char c = arr[i];
            castlingAbility[indexOf(NAMES_LOOKUP, c)] = true;
        }
    }

    private static int indexOf(char[] arr, char c) {
        for (int i = 0, arrLength = arr.length; i < arrLength; i++) {
            char t = arr[i];
            if (t == c)
                return i;
        }
        return -1;
    }

    public static boolean checkIfAllFalse(boolean[] arr) {
        for (boolean b : arr)
            if (b)
                return false;
        return true;
    }

    public boolean getCastlingAbility(int player, int side) {
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
        String ret = "";
        if (Arrays.hashCode(castlingAbility) == allFalseHash)
            return NO_CASTLING_ABILITY;
        for (int i = 0; i < castlingAbility.length; i++) {
            if (castlingAbility[i]) {
                ret += NAMES_LOOKUP[i] + "";
            }
        }
        return ret;
    }

    public boolean[] getCastlingAbility(int player) {
        player *= 2;
        return Arrays.copyOfRange(castlingAbility, player, player + 2);
    }
}
