package ver14.Game;

import ver14.DB.DB;
import ver14.Players.Player;
import ver14.Server;
import ver14.SharedClasses.Callbacks.AnswerCallback;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.SavedGames.ArchivedGameInfo;
import ver14.SharedClasses.Game.SavedGames.EstablishedGameInfo;
import ver14.SharedClasses.Game.SavedGames.GameInfo;
import ver14.SharedClasses.Game.SavedGames.UnfinishedGame;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.Sync.SyncableItem;
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

public class GameSession extends HandledThread implements SyncableItem {
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
        setName("Game Session #" + gameID);
        this.gameID = gameID;
        this.server = server;
        this.creator = creator;
        this.p2 = p2;
        game = new Game(creator, p2, gameSettings, this);
        game.forEachPlayer(p -> p.setGameSession(this));

        addHandler(MyError.DisconnectedError.class, e -> {
            if (e instanceof Game.PlayerDisconnectedError playerDisconnected) {
                game.interruptRead(playerDisconnected.createGameStatus());
            } else throw new MyError(e);
        });
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void handledRun() {
        GameStatus gameOver;

        do {
            gameOver = game.startNewGame();
            saveGame(gameOver);
            GameStatus finalGameOver = gameOver;
            game.parallelForEachPlayer(player -> player.gameOver(finalGameOver));
            if (gameOver.isDisconnected())
                break;
        } while (askForRematch());

        server.endOfGameSession(this);
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
            log("saving unfinished game: " + unfinishedGame);
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
            log("saving game result: " + archivedGameInfo);
            DB.saveGameResult(archivedGameInfo);

        }
    }

    public boolean askForRematch() {
        if (getPlayers().stream().anyMatch(p -> !p.isConnected()))
            return false;

        AtomicBoolean atomicBoolean = new AtomicBoolean(true);

        AtomicInteger numOfRes = new AtomicInteger(0);

        CompletableFuture<Boolean> rematch = new CompletableFuture<>();

        AtomicReference<Player> canceledPlayer = new AtomicReference<>();
        getPlayers().forEach(player -> {
            player.askQuestion(Question.Rematch, ans -> {
                synchronized (atomicBoolean) {
                    if (!player.isConnected() || !player.getPartner().isConnected() || (atomicBoolean.get() && !ans.equals(Question.Answer.YES))) {
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
            log("rematch = " + res);
            if (!res) {
                getPlayers().forEach(player ->
                        player.cancelQuestion(Question.Rematch, canceledPlayer.get().getUsername() + " didnt want to rematch")
                );
            }
            return res;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isSaveWorthy(GameStatus gameResult) {
        if (gameResult.getGameStatusType() == GameStatus.GameStatusType.UNFINISHED) {
            return creator.isSaveWorthy() && p2.isAi();
        }
        return creator.isSaveWorthy() || p2.isSaveWorthy();
    }

    public void log(String str) {
        server.log(this + "-->" + str);
    }

    public List<Player> getPlayers() {
        return List.of(creator, p2);
    }

    public void playerDisconnected(Player player) {
        game.interruptRead(GameStatus.playerDisconnected(player.getPlayerColor(), player.getPartner().isAi()));
    }

    @Override
    public String toString() {
        return "Session(%s) %s".formatted(gameID, game);
    }

    @Override
    public SyncableItem getSyncableItem() {
        return game.getCurrentGameState();
    }

    @Override
    public String ID() {
        return gameID;
    }

    public void resigned(Player player) {
        game.interruptRead(GameStatus.playerResigned(player.getPlayerColor()));
    }

    public String sessionsDesc() {
        return "Session(%s) %s vs %s".formatted(gameID, StrUtils.dontCapWord(creator.getUsername()), StrUtils.dontCapWord(p2.getUsername()));
    }

    public void askedQuestion(Player player, Question question) {
        log(player.getUsername() + " asked " + question);
        player.getPartner().askQuestion(question, ans -> {
            log(player.getPartner() + " responded with a " + ans + " to " + question);
            getAnswerHandler(question).callback(ans);
        });
    }

    private AnswerCallback getAnswerHandler(Question question) {
        return switch (question.questionType) {
            case DRAW_OFFER -> ans -> {
                if (ans == Question.Answer.ACCEPT) {
                    game.interruptRead(GameStatus.tieByAgreement());
                }
            };
            case THREEFOLD -> null;
            default -> null;
        };
    }
}
