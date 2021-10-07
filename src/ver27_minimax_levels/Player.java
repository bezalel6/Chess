package ver27_minimax_levels;

public class Player {
    public static final int WHITE = 0, BLACK = 1, NO_PLAYER = 2;
    public static final int[] PLAYERS = {WHITE, BLACK};
    public static final String[] PLAYER_NAMES = {"White", "Black", "No Player"};

    public static boolean isValidPlayer(int player) {
        return player == WHITE || player == BLACK;
    }

    public static int getOpponent(int currentPlayer) {
        return Math.abs((currentPlayer - 1) * -1);
    }

}