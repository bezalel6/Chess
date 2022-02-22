package ver7.server.game;

import ver7.SharedClasses.GameSettings;
import ver7.SharedClasses.PlayerColor;
import ver7.SharedClasses.SavedGames.ArchivedGameInfo;
import ver7.SharedClasses.SavedGames.EstablishedGameInfo;
import ver7.SharedClasses.SavedGames.GameInfo;
import ver7.SharedClasses.SavedGames.UnfinishedGame;
import ver7.SharedClasses.Sync.SyncableItem;
import ver7.SharedClasses.evaluation.GameStatus;
import ver7.SharedClasses.moves.Move;
import ver7.SharedClasses.networking.MyErrors;
import ver7.server.DB.DB;
import ver7.server.Server;
import ver7.server.players.Player;

import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameSession extends Thread implements SyncableItem {
    public final String gameID;
    private final Server server;
    private final Game game;
    private final Player creator;
    private final Player p2;


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
        } while (askForRematch());

////        todo move ↓ to the endOfGameSession
//        game.parallelForEachPlayer(p -> {
//            try {
//                server.gameSetup(p);
//            } catch (MyErrors e) {
//                e.printStackTrace();
//            }
//        });

        server.endOfGameSession(this);

    }

    public void saveGame(GameStatus gameResult) {
        if (!isSaveWorthy()) {
            return;
        }
        GameSettings gameSettings = game.getGameSettings();
        String un = creator.getUsername();
        String opp = creator.getPartner().getUsername();
        Stack<Move> moveStack = game.getMoveStack();
        if (gameResult.getGameStatusType() == GameStatus.GameStatusType.UNFINISHED) {
            UnfinishedGame unfinishedGame = new UnfinishedGame(
                    gameID,
                    un,
                    gameSettings,
                    opp,
                    gameSettings.getPlayerToMove(),
                    game.getCurrentPlayer().getUsername(),
                    moveStack);
            try {
                DB.saveUnFinishedGame(unfinishedGame);
            } catch (MyErrors e) {
                e.printStackTrace();
            }
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
            try {
                DB.saveGameResult(archivedGameInfo);
            } catch (MyErrors e) {
                e.printStackTrace();
            }
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

    public boolean isSaveWorthy() {
        return creator.isSaveWorthy() || p2.isSaveWorthy();
    }

    public Player[] getPlayers() {
        return new Player[]{creator, p2};
    }

    public void log(String str) {
        server.log(str);
    }

    @Override
    public String ID() {
        return gameID;
    }
}
