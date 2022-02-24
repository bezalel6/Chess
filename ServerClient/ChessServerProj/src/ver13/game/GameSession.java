package ver13.game;

import ver13.DB.DB;
import ver13.Server;
import ver13.SharedClasses.GameSettings;
import ver13.SharedClasses.PlayerColor;
import ver13.SharedClasses.SavedGames.ArchivedGameInfo;
import ver13.SharedClasses.SavedGames.EstablishedGameInfo;
import ver13.SharedClasses.SavedGames.GameInfo;
import ver13.SharedClasses.SavedGames.UnfinishedGame;
import ver13.SharedClasses.Sync.SyncableItem;
import ver13.SharedClasses.evaluation.GameStatus;
import ver13.SharedClasses.moves.Move;
import ver13.players.Player;

import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameSession extends Thread implements SyncableItem {
    public final String gameID;
    private final Server server;
    private final Game game;
    private final Player creator;
    private final Player p2;
    private boolean isPlaying = false;

    public GameSession(UnfinishedGame unfinishedGame, Player creator, Player otherPlayer, Server server) {
        this((EstablishedGameInfo) unfinishedGame, creator, otherPlayer, server);
        PlayerColor clr = unfinishedGame.playerColorToMove;
        game.setCreatorColor(unfinishedGame.isCreatorToMove() ? clr : clr.getOpponent());
    }

    public GameSession(EstablishedGameInfo gameInfo, Player creator, Player otherPlayer, Server server) {
        this((GameInfo) gameInfo, creator, otherPlayer, server);
        game.setMoveStack(gameInfo.getMoveStack());
    }

    public GameSession(GameInfo gameInfo, Player creator, Player otherPlayer, Server server) {
        this(gameInfo.gameId, creator, otherPlayer, gameInfo.gameSettings, server);
    }

    public GameSession(String gameID, Player creator, Player p2, GameSettings gameSettings, Server server) {
        this.gameID = gameID;
        this.server = server;
        this.creator = creator;
        this.p2 = p2;
        game = new Game(creator, p2, gameSettings, this);
        game.forEachPlayer(p -> p.setGameSession(this));
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void run() {
        GameStatus gameOver;
        Player disconnectedPlayer;
        do {
            gameOver = game.startNewGame();
            saveGame(gameOver);
            GameStatus finalGameOver = gameOver;
            game.parallelForEachPlayer(player -> player.gameOver(finalGameOver));
            disconnectedPlayer = game.getDisconnectedPlayer();
        } while (disconnectedPlayer != null && askForRematch());

        server.endOfGameSession(this, disconnectedPlayer);
    }

    public void saveGame(GameStatus gameResult) {
        if (!isSaveWorthy(gameResult)) {
            return;
        }
        GameSettings gameSettings = game.getGameSettings();
        String un = creator.getUsername();
        String opp = creator.getPartner().getUsername();
        Stack<Move> moveStack = game.getMoveStack();
        if (gameResult.getGameStatusType() == GameStatus.GameStatusType.UNFINISHED) {
            //if reached this point there's no need to verify vs ai. would've returned already if it isnt
            UnfinishedGame unfinishedGame = new UnfinishedGame(
                    gameID,
                    un,
                    gameSettings,
                    opp,
                    gameSettings.getPlayerToMove(),
                    game.getCurrentPlayer().getUsername(),
                    moveStack
            );
            server.log("saving unfinished game: " + unfinishedGame);
            DB.saveUnFinishedGame(unfinishedGame);
        } else {
            String winner = switch (gameResult.getGameStatusType()) {
                case TIE -> DB.TIE_STR;
                case WIN_OR_LOSS -> gameResult.getWinningColor().equals(creator.getPlayerColor()) ? creator.getUsername() : creator.getPartner().getUsername();

                default -> throw new IllegalStateException("Unexpected value: " + gameResult.getGameStatusType());
            };
            ArchivedGameInfo archivedGameInfo = new ArchivedGameInfo(
                    gameID,
                    un,
                    opp,
                    gameSettings,
                    winner,
                    moveStack
            );
            server.log("saving game result: " + archivedGameInfo);
            DB.saveGameResult(archivedGameInfo);

        }
    }

    public boolean askForRematch() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        Arrays.stream(getPlayers()).parallel().forEach(player -> {
            boolean res = player.askForRematch();
            if (atomicBoolean.get() && !res) {
                atomicBoolean.set(false);
                player.getPartner().cancelRematch();
            }
        });
        return atomicBoolean.get();
    }

    public boolean isSaveWorthy(GameStatus gameResult) {
        if (gameResult.getGameStatusType() == GameStatus.GameStatusType.UNFINISHED) {
            return game.getGameSettings().isVsAi();
        }
        return creator.isSaveWorthy() || p2.isSaveWorthy();
    }

    public Player[] getPlayers() {
        return new Player[]{creator, p2};
    }

    @Override
    public SyncableItem getSyncableItem() {
        return game.getCurrentGameState();
    }

    @Override
    public String ID() {
        return gameID;
    }

    public void log(String str) {
        server.log(str);
    }
}
