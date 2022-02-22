package ver5.server.game;

import ver5.SharedClasses.GameSettings;
import ver5.SharedClasses.SavedGame;
import ver5.SharedClasses.evaluation.GameStatus;
import ver5.server.DB.DB;
import ver5.server.Server;
import ver5.server.players.Player;
import ver5.server.players.PlayerNet;

import java.sql.SQLException;

public class GameSession extends Thread {
    public final String gameID;
    private final Server server;
    private final Game game;

    public GameSession(SavedGame savedGame, Player creator, Player otherPlayer, Server server) {
        this(savedGame.gameId, creator, otherPlayer, savedGame.gameSettings, server);
    }

    public GameSession(String gameID, Player creator, Player p2, GameSettings gameSettings, Server server) {
        this.gameID = gameID;
        this.server = server;
        game = new Game(creator, p2, gameSettings, this);
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void run() {
        do {
            GameStatus gameOver = game.startNewGame();
            saveGame(gameOver);
            game.forEachPlayer(player -> player.gameOver(gameOver));
        } while (game.askForRematch());

        game.parallelForEachPlayer(player -> {
            if (player instanceof PlayerNet playerNet)
                server.gameSetup(playerNet);
        });
    }

    public void saveGame(GameStatus gameStatus) {
        try {
            Player creator = game.getGameCreator();
            String winner = switch (gameStatus.getGameStatusType()) {
                case TIE -> DB.TIE_STR;
                case WIN_OR_LOSS -> gameStatus.getWinningColor().equals(creator.getPlayerColor()) ? creator.getUsername() : creator.getPartner().getUsername();
                case UNFINISHED -> null;

                default -> throw new IllegalStateException("Unexpected value: " + gameStatus.getGameStatusType());
            };
            GameSettings gameSettings = game.getGameSettings();
            DB.saveGame(new SavedGame(gameID, creator.getUsername(), creator.getPartner().getUsername(), gameSettings, winner));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void log(String str) {
        server.log(str);
    }
}
