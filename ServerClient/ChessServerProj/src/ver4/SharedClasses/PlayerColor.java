package ver4.SharedClasses;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum PlayerColor implements Serializable {
    WHITE, BLACK, NO_PLAYER;
    public static final PlayerColor[] PLAYER_COLORS = {WHITE, BLACK};
    public static final int NUM_OF_PLAYERS = 2;
    private static final String[] PLAYER_NAMES = {"White", "Black", "No Player"};
    private static final Map<Integer, PlayerColor> map = new HashMap<>();

    static {
        for (PlayerColor p : values()) {
            map.put(p.asInt(), p);
        }
    }

    public static boolean isValidPlayer(PlayerColor playerColor) {
        return playerColor == WHITE || playerColor == BLACK;
    }

    public static PlayerColor getOpponent(PlayerColor currentPlayerColor) {
        return valueOf(Math.abs((currentPlayerColor.ordinal() - 1) * -1));
    }

    public static PlayerColor valueOf(int p) {
        return map.get(p);
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

    public String getName() {
        return PLAYER_NAMES[asInt()];
    }

    public PlayerColor getOpponent() {
        return getOpponent(this);
    }

    public int asInt() {
        return ordinal();
    }

    @Override
    public String toString() {
        return getName();
    }
}