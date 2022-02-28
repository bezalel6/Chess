package ver13.SharedClasses;

import ver13.SharedClasses.Utils.StrUtils;

public enum PlayerColor {
    WHITE {
        @Override
        public PlayerColor getOpponent() {
            return BLACK;
        }
    }, BLACK {
        @Override
        public PlayerColor getOpponent() {
            return WHITE;
        }
    }, NO_PLAYER {
        @Override
        public PlayerColor getOpponent() {
            return NO_PLAYER;
        }
    };
    public static final PlayerColor[] PLAYER_COLORS = {WHITE, BLACK};
    public static final int NUM_OF_PLAYERS = 2;

    public final int asInt;
    public final int indexOf2;
//    public final int startingRow;

    PlayerColor() {
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