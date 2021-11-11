package ver34_faster_move_generation.model_classes;

import ver34_faster_move_generation.Player;


public class CastlingAbility {
    public static final String NO_CASTLING_ABILITY = "-";
    public static final int W = 0b1100, B = 0b11;
    public static final int KING_SIDE = 0, QUEEN_SIDE = 1;
    private static final char[] NAMES_LOOKUP = new char[]{'K', 'Q', 'k', 'q'};
    private static final int[] BYTES_LOOKUP = new int[]{0b1000, 0b0100, 0b10, 0b1};
    private static final int[] PLAYER_INDEX = initPlayerIndex();

    public static void main(String[] args) {
        int n = createFromStr("KQkq");
        System.out.println(Integer.toBinaryString(n));
        System.out.println("White: " + Integer.toBinaryString(getPlayerCastling(Player.WHITE, n)));
        System.out.println("Black: " + Integer.toBinaryString(getPlayerCastling(Player.BLACK, n)));
        n = disableCastling(Player.WHITE, KING_SIDE, n);
        n = disableCastling(Player.WHITE, KING_SIDE, n);
        n = disableCastling(Player.WHITE, QUEEN_SIDE, n);
        n = disableCastling(Player.WHITE, KING_SIDE, n);
        System.out.println("White: " + Integer.toBinaryString(getPlayerCastling(Player.WHITE, n)));
        System.out.println("Black: " + Integer.toBinaryString(getPlayerCastling(Player.BLACK, n)));
    }

    public static boolean checkCastling(int player, int side, int castlingAbility) {
        return (getPlayerCastling(player, castlingAbility) & BYTES_LOOKUP[2 + side]) != 0;
    }

    public static int disableCastling(int player, int side, int castlingAbility) {
        int mask = BYTES_LOOKUP[PLAYER_INDEX[player] + side];
        mask = ~mask;
        return castlingAbility & mask;
    }

    public static boolean hasAnyCastlingRights(int player, int castlingAbility) {
        return getPlayerCastling(player, castlingAbility) != 0;
    }

    public static int disableCastlingForBothSides(int player, int castlingAbility) {
        castlingAbility = disableCastling(player, KING_SIDE, castlingAbility);
        castlingAbility = disableCastling(player, QUEEN_SIDE, castlingAbility);
        return castlingAbility;
    }

    private static int[] initPlayerIndex() {
        int[] ret = new int[2];
        ret[Player.BLACK] = 2;
        ret[Player.WHITE] = 0;
        return ret;
    }

    public static int getPlayerCastling(int player, int castlingAbility) {
        int index = PLAYER_INDEX[player];
        int res = ((BYTES_LOOKUP[index] & castlingAbility) | (BYTES_LOOKUP[index + 1] & castlingAbility));
        res = res >> (player == Player.WHITE ? 2 : 0);
        return res;
    }

    public static int createFromStr(String castlingAbilityStr) {
        castlingAbilityStr = castlingAbilityStr.trim();
        if (castlingAbilityStr.equals("") || castlingAbilityStr.equals("-"))
            return 0;
        int ret = 0;
        for (char c : castlingAbilityStr.toCharArray()) {
            for (int i = 0; i < NAMES_LOOKUP.length; i++) {
                char value = NAMES_LOOKUP[i];
                if (c == value) {
                    ret = ret | BYTES_LOOKUP[i];
                    break;
                }

            }
        }
        return ret;
    }

    public static String generateCastlingAbilityStr(int castlingAbility) {
        if (castlingAbility == 0) {
            return NO_CASTLING_ABILITY;
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < NAMES_LOOKUP.length; i++) {
            if ((castlingAbility & BYTES_LOOKUP[i]) != 0) {
                str.append(NAMES_LOOKUP[i]);
            }
        }
        return str.toString();
    }
}
