package ver5.server.game;

import ver5.SharedClasses.GameSettings;
import ver5.SharedClasses.GameTime;
import ver5.SharedClasses.PlayerColor;
import ver5.SharedClasses.TimeFormat;
import ver5.SharedClasses.evaluation.GameStatus;
import ver5.SharedClasses.moves.Move;
import ver5.SharedClasses.ui.GameView;
import ver5.model_classes.Model;
import ver5.server.players.Player;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Game.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com)  10/11/2021
 */
public class Game {
    public static final int ROWS = 8;
    public static final int COLS = 8;
    private final static boolean showGameView = false;
    private final GameSession session;
    private final GameView gameView;
    private final Player gameCreator, p2;
    private final Model model;
    private final GameSettings startedGameSettings;
    private Player resignedPlayer;
    private GameSettings gameSettings;
    private GameTime gameTime;
    private PlayerColor timedOutPlayer;
    private volatile Player disconnectedPlayer;
    private Player currentPlayer;
    private volatile boolean isReadingMove = false;

    public Game(Player gameCreator, Player p2, GameSettings gameSettings, GameSession session) {
        this.session = session;
        this.gameCreator = gameCreator;
        this.p2 = p2;
        this.gameView = new GameView(showGameView);
        this.model = new Model();
        startedGameSettings = new GameSettings(gameSettings);
        this.disconnectedPlayer = null;
        setPartners();
    }

    private void setPartners() {
        gameCreator.setPartner(p2);
        p2.setPartner(gameCreator);
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public Player getGameCreator() {
        return gameCreator;
    }

    public GameTime getGameTime() {
        return gameTime;
    }

    public Model getModel() {
        return model;
    }

    public GameStatus startNewGame() {
        disconnectedPlayer = null;
        timedOutPlayer = null;
        resignedPlayer = null;
        initGame();
        return runGame();
    }

    private void initGame() {
        gameSettings = new GameSettings(startedGameSettings);
        model.setup(gameSettings.getFen());
        setColors();
        setCurrentPlayer();
        gameTime = new ServerGameTime(() -> timedOut(getCurrentPlayer().getPlayerColor()), TimeFormat.NORMAl);
        initPlayersGames();
    }

    private GameStatus runGame() {
        GameStatus gameOverStatus = null;
        session.log("Starting game " + this);
        gameView.update(model.getLogicBoard());
        gameTime.startTimer();
        while (gameOverStatus == null) {
            gameTime.runTime(currentPlayer.getPlayerColor());
            currentPlayer.getPartner().waitTurn();

            Move move = getMove(currentPlayer);
            GameStatus gameStatus = makeMove(move);

            if (gameStatus.isGameOver()) {
                gameOverStatus = gameStatus;
            } else {
                gameView.update(model.getLogicBoard());
                currentPlayer = currentPlayer.getPartner();
            }
        }
        gameSettings.setPlayerColor(currentPlayer.getPlayerColor());
        gameSettings.setFen(model.getFenStr());
        gameTime.stopTimer();
        return gameOverStatus;
    }

    private void setColors() {
        PlayerColor clr;
        if (gameSettings != null && gameSettings.getPlayerColor() != PlayerColor.NO_PLAYER) {
            clr = gameSettings.getPlayerColor();
        } else {
            clr = new Random().nextBoolean() ? PlayerColor.WHITE : PlayerColor.BLACK;
        }
        gameCreator.setPlayerColor(clr);
        gameCreator.getPartner().setPlayerColor(clr.getOpponent());
    }

    private void setCurrentPlayer() {
        currentPlayer = getCurrentPlayer();
    }

    public void timedOut(PlayerColor playerColor) {
        timedOutPlayer = playerColor;
    }

    private Player getCurrentPlayer() {
        return (gameCreator.getPlayerColor() == model.getCurrentPlayer() ? gameCreator : p2);
    }

    private void initPlayersGames() {
        forEachPlayer(p -> p.initGame(this));
    }

    private Move getMove(Player currentPlayer) {
        isReadingMove = true;
        Move move = currentPlayer.getMove();
        isReadingMove = false;
        if (move == null || disconnectedPlayer != null) {
            if (disconnectedPlayer == null)
                disconnectedPlayer = currentPlayer;
            return null;
        }

        move.getMoveAnnotation().resetPromotion(move);
        move.setMovingColor(currentPlayer.getPlayerColor());
        return move;
    }

    private GameStatus makeMove(Move move) {
        if (resignedPlayer != null) {
            return GameStatus.playerResigned(resignedPlayer.getPartner().getPlayerColor());
        }
        if (disconnectedPlayer != null) {
            return GameStatus.playerDisconnected(disconnectedPlayer.getPartner().getPlayerColor());
        }
        if (timedOutPlayer != null)
            return GameStatus.timedOut(timedOutPlayer);
        if (move.getThreefoldStatus() == Move.ThreefoldStatus.CLAIMED) {
            return GameStatus.threeFoldRepetition();
        }

        model.makeMove(move);
        forEachPlayer(player -> player.updateByMove(move));
        return move.getMoveEvaluation().getGameStatus();
    }

    public void forEachPlayer(a f) {
        f.func(gameCreator);
        f.func(p2);
    }

    public void parallelForEachPlayer(a f) {
        Arrays.stream(getPlayers()).parallel().forEach(p -> {
            if (p != null)
                f.func(p);
        });
    }

    public Player[] getPlayers() {
        return new Player[]{gameCreator, p2};
    }

    public void resigned(Player player) {
        resignedPlayer = player;
        interruptRead();
    }

    private void interruptRead() {
        if (isReadingMove) {
            currentPlayer.cancelRematch();
        }
    }

    public void playerDisconnected(Player player) {
        if (disconnectedPlayer == null) {
            disconnectedPlayer = player;
            interruptRead();
        }
    }

    public boolean askForRematch() {
        if (disconnectedPlayer != null)
            return false;
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

    @Override
    public String toString() {
        return "Game{" +
                "p1=" + gameCreator +
                ", p2=" + p2 +
                ", gameSettings=" + gameSettings +
                '}';
    }

    interface a {
        void func(Player player);
    }
}
