package ver14.SharedClasses.Game.Moves;


import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Locale;


/**
 * Castling rights - represents castling rights for both players in a position .
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class CastlingRights implements Serializable {
    /**
     * The constant NO_CASTLING_ABILITY.
     */
    public static final String NO_CASTLING_ABILITY = "-";

    /**
     * The constant RIGHTS.
     */
    private final static byte[] RIGHTS = new byte[4];
    /**
     * The constant PLAYER_MASKS.
     */
    private final static byte[] PLAYER_MASKS = new byte[2];
    /**
     * The constant FENS.
     */
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

    /**
     * The Rights.
     */
    private byte rights;

    /**
     * Instantiates a new Castling rights.
     *
     * @param rights the rights
     */
    public CastlingRights(byte rights) {
        this.rights = rights;
    }

    /**
     * Instantiates a new Castling rights.
     */
    public CastlingRights() {
        rights = 0;
    }

    /**
     * Instantiates a new Castling rights.
     *
     * @param other the other
     */
    public CastlingRights(CastlingRights other) {
        rights = other.rights;
    }


    /**
     * Create from str castling rights.
     *
     * @param castlingAbilityStr the castling ability str
     * @return the castling rights
     */
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

    /**
     * Enable castling.
     *
     * @param playerColor the player color
     * @param side        the side
     */
    public void enableCastling(PlayerColor playerColor, Side side) {
        rights |= getRights(playerColor, side);
    }

    /**
     * Gets rights.
     *
     * @param playerColor the player color
     * @param side        the side
     * @return the rights
     */
    private static byte getRights(PlayerColor playerColor, Side side) {
        return RIGHTS[getPlayerAndSideIndex(playerColor, side)];
    }

    /**
     * Gets player and side index.
     *
     * @param playerColor the player color
     * @param side        the side
     * @return the player and side index
     */
    private static int getPlayerAndSideIndex(PlayerColor playerColor, Side side) {
        return playerColor.indexOf2 + side.asInt;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {

    }

    /**
     * Whos castling player color.
     *
     * @param castlingRights the castling rights
     * @return the player color
     */
    public static PlayerColor whosCastling(byte castlingRights) {
        return (PLAYER_MASKS[PlayerColor.WHITE.asInt] & castlingRights) != 0 ? PlayerColor.WHITE : PlayerColor.BLACK;
    }

    /**
     * To string string.
     *
     * @return the string
     */
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

    /**
     * Is enabled boolean.
     *
     * @param playerColor the player color
     * @param side        the side
     * @return the boolean
     */
    public boolean isEnabled(PlayerColor playerColor, Side side) {
        return (rights & getRights(playerColor, side)) != 0;
    }

    /***
     *
     * @param playerColor the player color
     * @return disabled bytes
     */
    public byte disableCastling(PlayerColor playerColor) {
        return (byte) (disableCastling(playerColor, Side.SIDES[0]) |
                disableCastling(playerColor, Side.SIDES[1]));
    }

    /**
     * Disable castling byte.
     *
     * @param playerColor the player color
     * @param side        the side
     * @return the byte
     */
    public byte disableCastling(PlayerColor playerColor, Side side) {
        byte old = rights;
        rights &= ~getRights(playerColor, side);
        return (byte) (old ^ rights);
    }

    /**
     * Gets rights.
     *
     * @return the rights
     */
    public byte getRights() {
        return rights;
    }

    /**
     * Enable.
     *
     * @param b the b
     */
    public void enable(byte b) {
        rights |= b;
    }

    /**
     * Has any boolean.
     *
     * @param playerColor the player color
     * @return the boolean
     */
    public boolean hasAny(PlayerColor playerColor) {
        return getPlayersCastling(playerColor) != 0;
    }

    /**
     * Gets players castling.
     *
     * @param playerColor the player color
     * @return the players castling
     */
    public byte getPlayersCastling(PlayerColor playerColor) {
        return (byte) (rights & PLAYER_MASKS[playerColor.asInt]);
    }

    /**
     * Side - Castling side.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public enum Side {
        /**
         * King side.
         */
        KING(Location.G, Location.H, Location.F),
        /**
         * Queen side.
         */
        QUEEN(Location.C, Location.A, Location.D);

        /**
         * The constant SIDES.
         */
        public static final Side[] SIDES = {KING, QUEEN};
        /**
         * The Rook starting col.
         */
        public final int rookStartingCol;
        /**
         * The Castled rook col.
         */
        public final int castledRookCol;
        /**
         * The Castled king col.
         */
        public final int castledKingCol;
        /**
         * The King travel distance.
         */
        public final int kingTravelDistance;
        /**
         * The Castling notation.
         */
        public final String castlingNotation;
        /**
         * The As int.
         */
        public final int asInt;
        /**
         * The King Movement Direction Mult
         */
        public final int mult;

        /**
         * Instantiates a new Side.
         *
         * @param castledKingCol  the castled king col
         * @param rookStartingCol the rook starting col
         * @param castledRookCol  the castled rook col
         */
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


        /**
         * To string string.
         *
         * @return the string
         * @hidden
         */
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

        /**
         * King final loc location.
         *
         * @param currentKingLoc the current king loc
         * @return the location
         */
        public Location kingFinalLoc(Location currentKingLoc) {
            return Location.getLoc(currentKingLoc, kingTravelDistance * mult);
        }

    }
}
