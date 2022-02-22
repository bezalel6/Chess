package ver3.view;


public class CastlingAbility {
    public static final String NO_CASTLING_ABILITY = "-";
    public static final int W = 0b1100, B = 0b11;
    public static final int KING_SIDE = 0, QUEEN_SIDE = 1;
    public static final int[] ROOK_STARTING_COL;
    public static final int[] CASTLED_ROOK_COL;
    private static final char[] NAMES_LOOKUP = new char[]{'K', 'Q', 'k', 'q'};
    private static final int[] BYTES_LOOKUP = new int[]{1 << 3, 1 << 2, 1 << 1, 1};
    private static final int[] PLAYER_INDEX = initPlayerIndex();

    static {
        ROOK_STARTING_COL = new int[2];
        ROOK_STARTING_COL[KING_SIDE] = 7;
        ROOK_STARTING_COL[QUEEN_SIDE] = 0;

        CASTLED_ROOK_COL = new int[2];
        CASTLED_ROOK_COL[KING_SIDE] = 5;
        CASTLED_ROOK_COL[QUEEN_SIDE] = 3;

    }

    public static boolean[] getCastling(Player player, int castlingAbility) {
        return new boolean[]{checkCastling(player, KING_SIDE, castlingAbility), checkCastling(player, QUEEN_SIDE, castlingAbility)};
    }

    public static boolean checkCastling(Player player, int side, int castlingAbility) {
        return (getPlayerCastling(player, castlingAbility) & BYTES_LOOKUP[2 + side]) != 0;
    }

    public static int disableCastling(Player player, int side, int castlingAbility) {
        int mask = BYTES_LOOKUP[PLAYER_INDEX[player.asInt()] + side];
        mask = ~mask;
        return castlingAbility & mask;
    }

    public static boolean hasAnyCastlingRights(Player player, int castlingAbility) {
        return getPlayerCastling(player, castlingAbility) != 0;
    }

    public static int disableCastlingForBothSides(Player player, int castlingAbility) {
        castlingAbility = disableCastling(player, KING_SIDE, castlingAbility);
        castlingAbility = disableCastling(player, QUEEN_SIDE, castlingAbility);
        return castlingAbility;
    }

    private static int[] initPlayerIndex() {
        int[] ret = new int[2];
        ret[Player.BLACK.asInt()] = 2;
        ret[Player.WHITE.asInt()] = 0;
        return ret;
    }

    public static int getPlayerCastling(Player player, int castlingAbility) {
        int index = PLAYER_INDEX[player.asInt()];
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
