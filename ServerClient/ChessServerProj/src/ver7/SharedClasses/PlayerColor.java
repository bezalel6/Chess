package ver7.SharedClasses;

import java.util.HashMap;
import java.util.Map;

public enum PlayerColor {
    WHITE, BLACK, NO_PLAYER;
    public static final PlayerColor[] PLAYER_COLORS = {WHITE, BLACK};
    public static final int NUM_OF_PLAYERS = 2;
    private static final Map<Integer, PlayerColor> map = new HashMap<>();

    static {
        for (PlayerColor p : values()) {
            map.put(p.asInt(), p);
        }
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

    public int indexOf2() {
        return asInt() * 2;
    }

    public int asInt() {
        return ordinal();
    }

    public PlayerColor getOpponent() {
        return getOpponent(this);
    }

    public static PlayerColor getOpponent(PlayerColor currentPlayerColor) {
        if (currentPlayerColor == NO_PLAYER)
            return NO_PLAYER;
        return valueOf(Math.abs((currentPlayerColor.ordinal() - 1) * -1));
    }

    public static PlayerColor valueOf(int p) {
        return map.get(p);
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return StrUtils.format(name());
    }
}