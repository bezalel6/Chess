package ver14.SharedClasses.Game;

import ver14.SharedClasses.Utils.StrUtils;


/**
 * represents a player color.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum PlayerColor {
    /**
     * White.
     */
    WHITE(Location.WHITE_STARTING_ROW, Location.WHITE_DIFF) {
        @Override
        public PlayerColor getOpponent() {
            return BLACK;
        }
    },
    /**
     * Black.
     */
    BLACK(Location.BLACK_STARTING_ROW, Location.BLACK_DIFF) {
        @Override
        public PlayerColor getOpponent() {
            return WHITE;
        }
    },
    /**
     * No player.
     */
    NO_PLAYER(-1, 0) {
        @Override
        public PlayerColor getOpponent() {
            return NO_PLAYER;
        }
    };
    /**
     * The constant PLAYER_COLORS.
     */
    public static final PlayerColor[] PLAYER_COLORS = {WHITE, BLACK};
    /**
     * The constant NUM_OF_PLAYERS.
     */
    public static final int NUM_OF_PLAYERS = 2;

    /**
     * The As int.
     */
    public final int asInt;
    /**
     * index of two. calculated at initialization for performance. calculation: {@link #asInt}*2
     */
    public final int indexOf2;
    /**
     * The player's Starting row.
     */
    public final int startingRow;
    /**
     * The moving up ratio.
     */
    public final int diff;

    /**
     * Instantiates a new Player color.
     *
     * @param startingRow the starting row
     * @param diff        the diff
     */
    PlayerColor(int startingRow, int diff) {
        this.startingRow = startingRow;
        this.diff = diff;
        this.asInt = ordinal();
        this.indexOf2 = asInt * 2;
    }

    /**
     * Gets color.
     *
     * @param clr the clr
     * @return the color
     */
    public static PlayerColor getColor(int clr) {
        return PLAYER_COLORS[clr];
    }

    /**
     * Gets player from fen.
     *
     * @param playerToMove the player to move
     * @return the player from fen
     */
    public static PlayerColor getPlayerFromFen(String playerToMove) {
        return switch (playerToMove) {
            case "w" -> WHITE;
            case "b" -> BLACK;
            default -> throw new Error();
        };
    }

    /**
     * Gets opponent.
     *
     * @return the opponent
     */
    public abstract PlayerColor getOpponent();

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Gets the players name.
     *
     * @return the players name
     */
    public String getName() {
        return StrUtils.format(name().toLowerCase());
    }
}