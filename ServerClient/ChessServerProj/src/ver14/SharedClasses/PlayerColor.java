package ver14.SharedClasses;

import ver14.SharedClasses.Utils.StrUtils;

public enum PlayerColor {
    WHITE(Location.WHITE_STARTING_ROW, Location.WHITE_DIFF) {
        @Override
        public PlayerColor getOpponent() {
            return BLACK;
        }
    }, BLACK(Location.BLACK_STARTING_ROW, Location.BLACK_DIFF) {
        @Override
        public PlayerColor getOpponent() {
            return WHITE;
        }
    }, NO_PLAYER(-1, 0) {
        @Override
        public PlayerColor getOpponent() {
            return NO_PLAYER;
        }
    };
    public static final PlayerColor[] PLAYER_COLORS = {WHITE, BLACK};
    public static final int NUM_OF_PLAYERS = 2;

    public final int asInt;
    public final int indexOf2;
    public final int startingRow;
    public final int diff;

    PlayerColor(int startingRow, int diff) {
        this.startingRow = startingRow;
        this.diff = diff;
        this.asInt = ordinal();
        this.indexOf2 = asInt * 2;
    }

    public static PlayerColor getColor(int clr) {
        return PLAYER_COLORS[clr];
    }

    public static PlayerColor getPlayerFromFen(String playerToMove) {
        return switch (playerToMove) {
            case "w" -> WHITE;
            case "b" -> BLACK;
            default -> throw new Error();
        };
    }

    public abstract PlayerColor getOpponent();

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return StrUtils.format(name());
    }
}