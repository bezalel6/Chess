package ver14.SharedClasses.Game.Moves;


import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

/*
 * CastlingRights
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * CastlingRights -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * CastlingRights -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

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

    public static void main(String[] args) {

    }

    public static PlayerColor whosCastling(byte castlingRights) {
        return (PLAYER_MASKS[PlayerColor.WHITE.asInt] & castlingRights) != 0 ? PlayerColor.WHITE : PlayerColor.BLACK;
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
        KING(Location.G, Location.H, Location.F),
        QUEEN(Location.C, Location.A, Location.D);

        public static final Side[] SIDES = {KING, QUEEN};
        public final int rookStartingCol;
        public final int castledRookCol;
        public final int castledKingCol;
        public final int kingTravelDistance;
        public final String castlingNotation;
        public final int asInt;
        //King Movement Direction Mult
        public final int mult;

        Side(int castledKingCol, int rookStartingCol, int castledRookCol) {
            this.rookStartingCol = rookStartingCol;
            this.castledRookCol = castledRookCol;
            this.castledKingCol = castledKingCol;

            this.mult = -Integer.compare(4, castledKingCol);
//            always 2, but this is technically more dynamic
            this.kingTravelDistance = Math.abs(4 - castledKingCol);
            this.castlingNotation = StrUtils.repeat((i, isLast) -> "O" + (isLast ? "" : "-"), Math.abs(Location.KING_STARTING_COL - rookStartingCol) - 1);
            this.asInt = ordinal();
        }

        public static void main(String[] args) {
            Arrays.stream(values()).forEach(System.out::println);
        }

        @Override
        public String toString() {
            return name() + "{" +
                    "rookStartingCol=" + rookStartingCol +
                    ", castledRookCol=" + castledRookCol +
                    ", castledKingCol=" + castledKingCol +
                    ", kingTravelDistance=" + kingTravelDistance +
                    ", castlingNotation='" + castlingNotation + '\'' +
                    ", asInt=" + asInt +
                    ", mult=" + mult +
                    '}';
        }

        public Location kingFinalLoc(Location currentKingLoc) {
            return Location.getLoc(currentKingLoc, kingTravelDistance * mult);
        }

    }
}
