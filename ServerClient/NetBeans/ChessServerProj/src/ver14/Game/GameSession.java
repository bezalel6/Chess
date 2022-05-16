package ver14.Game;

import ver14.DB.DB;
import ver14.Players.Player;
import ver14.Server;
import ver14.SharedClasses.Callbacks.AnswerCallback;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.Evaluation.GameStatusType;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.SavedGames.ArchivedGameInfo;
import ver14.SharedClasses.Game.SavedGames.EstablishedGameInfo;
import ver14.SharedClasses.Game.SavedGames.GameInfo;
import ver14.SharedClasses.Game.SavedGames.UnfinishedGame;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.Sync.SyncableItem;
import ver14.SharedClasses.Threads.ErrorHandling.DisconnectedError;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Threads.HandledThread;
import ver14.SharedClasses.Utils.StrUtils;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Game session - represents a game session between two players. a session is running as long as both players are connected, and want to keep playing with each other.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class GameSession extends HandledThread implements SyncableItem {
    /**
     * The Game id.
     */
    public final String gameID;
    /**
     * The Server.
     */
    private final Server server;
    /**
     * The Game.
     */
    private final Game game;
    /**
     * The Creator.
     */
    private final Player creator;
    /**
     * The P 2.
     */
    private final Player p2;

    /**
     * Instantiates a new Game session. resuming an unfinished game.
     *
     * @param unfinishedGame the unfinished game
     * @param creator        the creator
     * @param otherPlayer    the other player
     * @param server         the server
     */
    public GameSession(UnfinishedGame unfinishedGame, Player creator, Player otherPlayer, Server server) {
        this((EstablishedGameInfo) unfinishedGame, creator, otherPlayer, server);
        PlayerColor clr = unfinishedGame.playerColorToMove;
        game.setCreatorColor(unfinishedGame.isCreatorToMove() ? clr : clr.getOpponent());
    }

    /**
     * Instantiates a new Game session. reloading a saved session
     *
     * @param gameInfo    the game info
     * @param creator     the creator
     * @param otherPlayer the other player
     * @param server      the server
     */
    public GameSession(EstablishedGameInfo gameInfo, Player creator, Player otherPlayer, Server server) {
        this((GameInfo) gameInfo, creator, otherPlayer, server);
        game.setMoveStack(gameInfo.getMoveStack());
    }

    /**
     * Instantiates a new Game session.
     *
     * @param gameInfo    the game info
     * @param creator     the creator
     * @param otherPlayer the other player
     * @param server      the server
     */
    public GameSession(GameInfo gameInfo, Player creator, Player otherPlayer, Server server) {
        this(gameInfo.gameId, creator, otherPlayer, gameInfo.gameSettings, server);
    }

    /**
     * Instantiates a new Game session.
     *
     * @param gameID       the game id
     * @param creator      the creator
     * @param p2           the p 2
     * @param gameSettings the game settings
     * @param server       the server
     */
    public GameSession(String gameID, Player creator, Player p2, GameSettings gameSettings, Server server) {
        setName("Game Session #" + gameID);
        this.gameID = gameID;
        this.server = server;
        this.creator = creator;
        this.p2 = p2;
        game = new Game(creator, p2, gameSettings, this);
        game.forEachPlayer(p -> p.setGameSession(this));

        addHandler(DisconnectedError.class, e -> {
            if (e instanceof PlayerDisconnectedError playerDisconnected) {
                game.interrupt(playerDisconnected.createGameStatus());
            } else throw new MyError(e);
        });
    }

    /**
     * Gets game.
     *
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * run the game inside the {@link HandledThread}'s 'container' with handlers setup for the relevant errors that might get thrown.
     */
    @Override
    public void handledRun() {
        do {
            GameStatus gameOver = game.runNewGame();
            saveGame(gameOver);
            game.parallelForEachPlayer(player -> player.gameOver(gameOver));
            if (gameOver.isDisconnected())
                break;
        } while (askForRematch());

        server.endOfGameSession(this);
    }

    /**
     * Save game.
     *
     * @param gameResult the game result
     */
    public void saveGame(GameStatus gameResult) {
        if (!isSaveWorthy(gameResult)) {
            return;
        }
        GameSettings gameSettings = game.getGameSettings();
        String un = creator.getUsername();
        String opp = creator.getPartner().getUsername();
        Stack<Move> moveStack = game.getMoveStack();
        if (gameResult.getGameStatusType() == GameStatusType.UNFINISHED) {
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
            log("saving unfinished game" + unfinishedGame.getGameDesc());
            DB.saveUnFinishedGame(unfinishedGame);
        } else {
            String winner = switch (gameResult.getGameStatusType()) {
                case TIE -> DB.TIE_STR;
                case WIN_OR_LOSS ->
                        gameResult.getWinningColor().equals(creator.getPlayerColor()) ? creator.getUsername() : creator.getPartner().getUsername();

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
            log("saving game result: " + archivedGameInfo.getGameDesc());
            DB.saveGameResult(archivedGameInfo);

        }
    }

    /**
     * Ask for rematch boolean.
     *
     * @return the boolean
     */
    public boolean askForRematch() {
        if (getPlayers().stream().anyMatch(p -> !p.isConnected()))
            return false;
//        System.out.println("both players are still connected");
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);

        AtomicInteger numOfRes = new AtomicInteger(0);

        CompletableFuture<Boolean> rematch = new CompletableFuture<>();

        AtomicReference<Player> canceledPlayer = new AtomicReference<>();
//        log("asking rematch");
        getPlayers().forEach(player -> {
            player.askQuestion(Question.Rematch, ans -> {
//                log(player + " ans = " + ans);
                synchronized (atomicBoolean) {
                    if (!player.isConnected() || !player.getPartner().isConnected() || (!ans.equals(Question.Answer.YES))) {
                        atomicBoolean.set(false);
                        rematch.complete(false);
                        if (canceledPlayer.get() == null)
                            canceledPlayer.set(player);
                    }

                    if (numOfRes.incrementAndGet() >= 2) {
                        rematch.complete(atomicBoolean.get());
                    }
                }

            });
        });

        try {
            boolean res = rematch.get();
//            log("rematch = " + res);
            if (!res) {
                getPlayers().forEach(player ->
                        player.cancelQuestion(Question.Rematch, canceledPlayer.get().getUsername() + " didnt want to rematch")
                );
            }
            return res;
        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
        }
        return false;
    }

    /**
     * Is save worthy boolean.
     *
     * @param gameResult the game result
     * @return the boolean
     */
    public boolean isSaveWorthy(GameStatus gameResult) {
        if (gameResult.getGameStatusType() == GameStatusType.UNFINISHED) {
            return creator.isSaveWorthy() && p2.isAi();
        }
        return creator.isSaveWorthy() || p2.isSaveWorthy();
    }

    /**
     * Log.
     *
     * @param str the str
     */
    public void log(String str) {
        server.log(this + "-->" + str);
    }

    /**
     * Gets players.
     *
     * @return the players
     */
    public List<Player> getPlayers() {
        return List.of(creator, p2);
    }

    /**
     * Player disconnected.
     *
     * @param player the player
     */
    public void playerDisconnected(Player player) {
        game.interrupt(GameStatus.playerDisconnected(player.getPlayerColor(), player.getPartner().isAi()));
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "Session(%s) %s".formatted(gameID, game);
    }

    /**
     * Gets syncable item.
     *
     * @return the syncable item
     */
    @Override
    public SyncableItem getSyncableItem() {
        return game.getCurrentGameState();
    }

    /**
     * Id string.
     *
     * @return the string
     */
    @Override
    public String ID() {
        return gameID;
    }

    /**
     * Resigned.
     *
     * @param player the player
     */
    public void resigned(Player player) {
        game.interrupt(GameStatus.playerResigned(player.getPlayerColor()));
    }

    /**
     * Sessions desc string.
     *
     * @return the string
     */
    public String sessionsDesc() {
        return "Session(%s) %s vs %s".formatted(gameID, StrUtils.dontCapWord(creator.getUsername()), StrUtils.dontCapWord(p2.getUsername()));
    }

    /**
     * Asked question.
     *
     * @param player   the player
     * @param question the question
     */
    public void askedQuestion(Player player, Question question) {
//        log(player.getUsername() + " asked " + question);
        player.getPartner().askQuestion(question, ans -> {
//            log(player.getPartner() + " responded with a " + ans + " to " + question);
            getAnswerHandler(question).callback(ans);
        });
    }

    /**
     * Gets answer handler.
     *
     * @param question the question
     * @return the answer handler
     */
    private AnswerCallback getAnswerHandler(Question question) {
        return switch (question.questionType) {
            case DRAW_OFFER -> ans -> {
                if (ans.equals(Question.Answer.ACCEPT)) {
                    game.interrupt(GameStatus.tieByAgreement());
                }
            };
            case THREEFOLD -> null;
            default -> null;
        };
    }

    /**
     * Server stop.
     *
     * @param cause the cause
     */
    public void serverStop(String cause) {
        game.interrupt(GameStatus.serverStoppedGame(cause));
    }
}
