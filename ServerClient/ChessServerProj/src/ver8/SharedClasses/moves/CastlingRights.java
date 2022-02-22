package ver8.SharedClasses.moves;


import ver8.SharedClasses.PlayerColor;

import java.io.Serializable;
import java.util.Locale;

public class CastlingRights implements Serializable {
    public static final String NO_CASTLING_ABILITY = "-";

    private final static byte[] RIGHTS = new byte[4];
    private final static String[] FENS = new String[4];

    static {
        for (PlayerColor playerColor : PlayerColor.PLAYER_COLORS) {
            for (Side side : Side.SIDES) {
                int index = getPlayerAndSideIndex(playerColor, side);
                RIGHTS[index] = (byte) (1 << (index));

                String str = side == Side.KING ? "k" : "q";
                str = playerColor == PlayerColor.WHITE ? str.toUpperCase() : str;
                FENS[index] = str;
            }
        }
    }

    private byte rights;

    public CastlingRights() {
        rights = 0;
    }

    public CastlingRights(CastlingRights other) {
        rights = other.rights;
    }


    public static CastlingRights createFromStr(String castlingAbilityStr) {
        CastlingRights ret = new CastlingRights();
        if (castlingAbilityStr.equals("") || castlingAbilityStr.equals(NO_CASTLING_ABILITY))
            return ret;
        for (char c : castlingAbilityStr.toCharArray()) {
            PlayerColor color = Character.isUpperCase(c) ? PlayerColor.WHITE : PlayerColor.BLACK;
            switch (Character.toLowerCase(c)) {
                case 'k' -> ret.enableCastling(color, Side.KING);
                case 'q' -> ret.enableCastling(color, Side.QUEEN);
            }
        }
        return ret;
    }

    public void enableCastling(PlayerColor playerColor, Side side) {
        rights |= getRights(playerColor, side);
    }

    private byte getRights(PlayerColor playerColor, Side side) {
        return RIGHTS[getPlayerAndSideIndex(playerColor, side)];
    }

    private static int getPlayerAndSideIndex(PlayerColor playerColor, Side side) {
        return playerColor.indexOf2() + side.asInt();
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (PlayerColor playerColor : PlayerColor.PLAYER_COLORS) {
            String currentStr = "";
            if (isEnabled(playerColor, Side.KING)) {
                currentStr += "k";
            }
            if (isEnabled(playerColor, Side.QUEEN)) {
                currentStr += "q";
            }
            ret.append(playerColor == PlayerColor.WHITE ? currentStr.toUpperCase(Locale.ROOT) : currentStr);
        }
        if (ret.toString().equals("")) {
            ret = new StringBuilder(NO_CASTLING_ABILITY);
        }
        return ret.toString();
    }

    public boolean isEnabled(PlayerColor playerColor, Side side) {
        return (rights & getRights(playerColor, side)) != 0;
    }

    public void disableCastling(PlayerColor playerColor) {
        for (Side side : Side.SIDES) {
            disableCastling(playerColor, side);
        }
    }

    public void disableCastling(PlayerColor playerColor, Side side) {
        rights &= ~getRights(playerColor, side);
    }

    public boolean hasAny(PlayerColor playerColor) {
        return getPlayersCastling(playerColor) != 0;
    }

    public byte getPlayersCastling(PlayerColor playerColor) {
        return (byte) ((rights & RIGHTS[playerColor.indexOf2()]) | rights & RIGHTS[playerColor.indexOf2() + 1]);
    }

    public enum Side {
        KING(7, 5, "O-O"),
        QUEEN(0, 3, "O-O-O");

        public static final Side[] SIDES = {KING, QUEEN};
        public final int rookStartingCol;
        public final int castledRookCol;
        public final String castlingNotation;

        Side(int rookStartingCol, int castledRookCol, String castlingNotation) {
            this.rookStartingCol = rookStartingCol;
            this.castledRookCol = castledRookCol;
            this.castlingNotation = castlingNotation;
        }

        public int asInt() {
            return ordinal();
        }
    }
}
