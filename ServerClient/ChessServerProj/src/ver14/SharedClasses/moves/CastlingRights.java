package ver14.SharedClasses.moves;


import ver14.SharedClasses.Location;
import ver14.SharedClasses.PlayerColor;

import java.io.Serializable;
import java.util.Locale;

public class CastlingRights implements Serializable {
    public static final String NO_CASTLING_ABILITY = "-";

    private final static byte[] RIGHTS = new byte[4];
    private final static byte[] PLAYER_MASKS = new byte[2];
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
            PLAYER_MASKS[playerColor.asInt] = (byte) (getRights(playerColor, Side.KING) | getRights(playerColor, Side.QUEEN));
        }
    }

    private byte rights;

    public CastlingRights(byte rights) {
        this.rights = rights;
    }

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

    private static byte getRights(PlayerColor playerColor, Side side) {
        return RIGHTS[getPlayerAndSideIndex(playerColor, side)];
    }

    private static int getPlayerAndSideIndex(PlayerColor playerColor, Side side) {
        return playerColor.indexOf2 + side.asInt;
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

    /***
     *
     * @param playerColor
     * @return disabled bytes
     *
     */
    public byte disableCastling(PlayerColor playerColor) {
        return (byte) (disableCastling(playerColor, Side.SIDES[0]) |
                disableCastling(playerColor, Side.SIDES[1]));
    }

    public byte disableCastling(PlayerColor playerColor, Side side) {
        byte old = rights;
        rights &= ~getRights(playerColor, side);
        return (byte) (old ^ rights);
    }

    public byte getRights() {
        return rights;
    }

    public void enable(byte b) {
        rights |= b;
    }

    public boolean hasAny(PlayerColor playerColor) {
        return getPlayersCastling(playerColor) != 0;
    }

    public byte getPlayersCastling(PlayerColor playerColor) {
        return (byte) (rights & PLAYER_MASKS[playerColor.asInt]);
    }

    public enum Side {
        KING(Location.H1.col, Location.F1.col, "O-O", 1),
        QUEEN(Location.A1.col, Location.D1.col, "O-O-O", -1);

        public static final Side[] SIDES = {KING, QUEEN};
        public final int rookStartingCol;
        public final int castledRookCol;
        public final String castlingNotation;
        public final int asInt;
        public final int mult;

        Side(int rookStartingCol, int castledRookCol, String castlingNotation, int mult) {
            this.rookStartingCol = rookStartingCol;
            this.castledRookCol = castledRookCol;
            this.castlingNotation = castlingNotation;
            this.mult = mult;
            this.asInt = ordinal();
        }
    }
}
