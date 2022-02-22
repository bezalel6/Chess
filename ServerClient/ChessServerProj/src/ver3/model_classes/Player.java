package ver3.model_classes;

import java.util.HashMap;
import java.util.Map;

public enum Player {
    WHITE, BLACK, NO_PLAYER;
    public static final Player[] PLAYERS = {WHITE, BLACK};
    public static final int NUM_OF_PLAYERS = 2;
    private static final String[] PLAYER_NAMES = {"White", "Black", "No Player"};
    private static final Map<Integer, Player> map = new HashMap<>();

    static {
        for (Player p : values()) {
            map.put(p.asInt(), p);
        }
    }

    public static boolean isValidPlayer(Player player) {
        return player == WHITE || player == BLACK;
    }

    public static Player getOpponent(Player currentPlayer) {
        return valueOf(Math.abs((currentPlayer.ordinal() - 1) * -1));
    }

    public static Player valueOf(int p) {
        return map.get(p);
    }

    public String getName() {
        return PLAYER_NAMES[asInt()];
    }

    public Player getOpponent() {
        return getOpponent(this);
    }

    public int asInt() {
        return ordinal();
    }

}