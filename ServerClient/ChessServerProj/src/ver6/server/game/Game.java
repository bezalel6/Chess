package ver6.server.game;

import ver6.SharedClasses.Callbacks.Callback;
import ver6.SharedClasses.GameSettings;
import ver6.SharedClasses.GameTime;
import ver6.SharedClasses.PlayerColor;
import ver6.SharedClasses.TimeFormat;
import ver6.SharedClasses.evaluation.GameStatus;
import ver6.SharedClasses.moves.Move;
import ver6.SharedClasses.networking.MyErrors;
import ver6.SharedClasses.ui.GameView;
import ver6.model_classes.Model;
import ver6.server.players.Player;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

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
    private final GameSettings originalSettings;
    private Stack<Move> moveStack;
    private Player resignedPlayer;
    private GameSettings gameSettings;
    private GameTime gameTime;
    private PlayerColor timedOutPlayer;
    private Player currentPlayer;
    private volatile Player disconnectedPlayer;
    private volatile boolean isReadingMove = false;
    private PlayerColor creatorColor = null;
    private boolean clearMoveStack = true;

    public Game(Player gameCreator, Player p2, GameSettings gameSettings, GameSession session) {
        this.moveStack = new Stack<>();
        this.session = session;
        this.gameCreator = gameCreator;
        this.p2 = p2;
        this.gameView = new GameView(showGameView);
        this.originalSettings = new GameSettings(gameSettings);
        this.model = new Model();
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

    public GameStatus startNewGame() throws MyErrors {
        disconnectedPlayer = null;
        timedOutPlayer = null;
        resignedPlayer = null;
        initGame();
        return runGame();
    }

    private void initGame() {
        if (clearMoveStack)
            moveStack.clear();
        else
            clearMoveStack = true;
        gameSettings = new GameSettings(originalSettings);
        model.setup(gameSettings.getFen());
        setColors();
        setCurrentPlayer();
        gameTime = new ServerGameTime(() -> timedOut(getCurrentPlayer().getPlayerColor()), TimeFormat.NORMAl);
        initPlayersGames();
    }

    private GameStatus runGame() throws MyErrors {
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
        gameSettings.setPlayerToMove(currentPlayer.getPlayerColor());
        gameSettings.setFen(model.getFenStr());
        gameTime.stopTimer();
        return gameOverStatus;
    }

    private void setColors() {

        PlayerColor clr;
        if (creatorColor != null) {
            clr = creatorColor;
        } else if (gameSettings != null && gameSettings.getPlayerToMove() != PlayerColor.NO_PLAYER) {
            clr = gameSettings.getPlayerToMove();
        } else {
            clr = new Random().nextBoolean() ? PlayerColor.WHITE : PlayerColor.BLACK;
        }
        gameCreator.setPlayerColor(clr);
        p2.setPlayerColor(clr.getOpponent());
    }

    private void setCurrentPlayer() {
        currentPlayer = getCurrentPlayer();
    }

    public void timedOut(PlayerColor playerColor) {
        timedOutPlayer = playerColor;
    }

    public Player getCurrentPlayer() {
        return (gameCreator.getPlayerColor() == model.getCurrentPlayer() ? gameCreator : p2);
    }


    private void initPlayersGames() {
        forEachPlayer(p -> p.initGame(this));
    }

    private Move getMove(Player currentPlayer) throws MyErrors {
        isReadingMove = true;
        Move move = currentPlayer.getMove();
        session.log("got move from " + currentPlayer + " " + move);
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
        moveStack.push(move);

        forEachPlayer(player -> player.updateByMove(move));
        return move.getMoveEvaluation().getGameStatus();
    }

    public void forEachPlayer(Callback<Player> callback) {
        callback.callback(gameCreator);
        callback.callback(p2);
    }

    public PlayerColor getCreatorColor() {
        return creatorColor;
    }

    public void setCreatorColor(PlayerColor creatorColor) {
        this.creatorColor = creatorColor;
    }

    public Stack<Move> getMoveStack() {
        return moveStack;
    }

    public void setMoveStack(Stack<Move> moveStack) {
        this.moveStack = moveStack;
        clearMoveStack = false;
    }

    public void parallelForEachPlayer(Callback<Player> callback) {
        Arrays.stream(getPlayers()).parallel().forEach(p -> {
            if (p != null)
                callback.callback(p);
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
            currentPlayer.interrupt();
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
//        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
//        Arrays.stream(getPlayers()).parallel().forEach(player -> {
//            boolean res = player.askForRematch();
//            if (atomicBoolean.get() && !res) {
//                atomicBoolean.set(false);
//                player.getPartner().cancelRematch();
//            }
//        });
//        return atomicBoolean.get();
        return false;
    }

    @Override
    public String toString() {
        return "Game{" +
                "p1=" + gameCreator +
                ", p2=" + p2 +
                ", gameSettings=" + gameSettings +
                '}';
    }


}
