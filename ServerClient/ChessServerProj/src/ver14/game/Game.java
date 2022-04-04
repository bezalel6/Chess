package ver14.game;

import ver14.Model.Model;
import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.GameTime;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.SavedGames.EstablishedGameInfo;
import ver14.SharedClasses.Game.SavedGames.UnfinishedGame;
import ver14.SharedClasses.Game.evaluation.GameStatus;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorType;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.ui.GameView;
import ver14.players.Player;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

/**
 * Game.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com)  10/11/2021
 */

//askilan move disconnected/resigned/... to game session
public class Game {
    public static final int ROWS = 8;
    public static final int COLS = 8;
    public static boolean showGameView = false;
    private final GameSession session;
    private final Player gameCreator, p2;
    private final GameSettings originalSettings;
    private final GameView gameView;
    private Model model;
    private Stack<Move> moveStack;
    private GameSettings gameSettings;
    private GameTime gameTime;
    private Player currentPlayer;
    private boolean isReadingMove = false;
    private PlayerColor creatorColor = null;
    private boolean clearMoveStack = true;

    public Game(Player gameCreator, Player p2, GameSettings gameSettings, GameSession session) {
        this.session = session;
        this.gameCreator = gameCreator;
        this.p2 = p2;

        this.originalSettings = new GameSettings(gameSettings);
        this.moveStack = new Stack<>();
        this.model = new Model();
        this.gameView = showGameView ? new GameView() : null;
        setPartners();
    }

    private void setPartners() {
        gameCreator.setPartner(p2);
        p2.setPartner(gameCreator);
    }

    public EstablishedGameInfo getCurrentGameState() {
        Player fakeCurrent = currentPlayer == null ? gameCreator : currentPlayer;

        return new UnfinishedGame(session.ID(),
                gameCreator.getUsername(),
                originalSettings,
                p2.getUsername(),
                fakeCurrent.getPlayerColor(),
                fakeCurrent.getUsername(),
                moveStack);
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
        this.moveStack = new Stack<>();
        this.model = new Model();
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
        assignColors();
        setCurrentPlayer();
        gameTime = new GameTime(gameSettings.getTimeFormat());
        initPlayersGames();

        session.log("Starting game " + this);

        updateDebugView();
    }

    private GameStatus runGame() {
        GameStatus gameOverStatus;
        while (true) {
            GameStatus gameStatus = playTurn();
            if (gameStatus.isGameOver()) {
                gameOverStatus = gameStatus;
                break;
            }
            switchTurn();
        }
        onGameOver();
        session.log("game over. " + gameOverStatus);
        return gameOverStatus;
    }

    private void assignColors() {
        PlayerColor creatorClr;
        if (creatorColor != null) {
            creatorClr = creatorColor;
        } else if (gameSettings != null && gameSettings.getPlayerToMove() != PlayerColor.NO_PLAYER) {
            creatorClr = gameSettings.getPlayerToMove();
        } else {
            creatorClr = new Random().nextBoolean() ? PlayerColor.WHITE : PlayerColor.BLACK;
        }
        gameCreator.setPlayerColor(creatorClr);
        p2.setPlayerColor(creatorClr.getOpponent());
    }

    private void setCurrentPlayer() {
        currentPlayer = getCurrentPlayer();
    }

    private void initPlayersGames() {
        forEachPlayer(p -> p.initGame(this));
    }

    private void updateDebugView() {
        if (this.gameView != null) {
            this.gameView.update(model.getLogicBoard());
        }
    }

    private GameStatus playTurn() {
//        gameTime.startRunning(currentPlayer.getPlayerColor());
        try {
            Move move = getMove();
            return makeMove(move);
        } catch (GameOverError error) {
            return error.gameOverStatus;
        }
    }

    private void switchTurn() {
        currentPlayer = currentPlayer.getPartner();
        gameTime.startRunning(currentPlayer.getPlayerColor());
        currentPlayer.getPartner().waitTurn();
        updateDebugView();
    }

    private void onGameOver() {
        gameSettings.setPlayerToMove(currentPlayer.getPlayerColor());
        gameSettings.setFen(model.genFenStr());
    }

    public Player getCurrentPlayer() {
        return (gameCreator.getPlayerColor() == model.getCurrentPlayer() ? gameCreator : p2);
    }

    public void forEachPlayer(Callback<Player> callback) {
        callback.callback(gameCreator);
        callback.callback(p2);
    }

    private Move getMove() {
        try {
            isReadingMove = true;
            Move move = currentPlayer.getMove();
            isReadingMove = false;
            session.log("move(%d) from %s: %s".formatted(moveStack.size() + 1, currentPlayer, move));//+1 bc current one isnt pushed yet

            move.setMovingColor(currentPlayer.getPlayerColor());
            return move;
        } catch (PlayerDisconnectedError error) {
            isReadingMove = false;
            throw new GameOverError(GameStatus.playerDisconnected(error.getDisconnectedPlayer().getPlayerColor(), error.disconnectedPlayer.getPartner().isAi()));
        } catch (MyError error) {
            isReadingMove = false;
            throw error;
        }
    }

    private GameStatus makeMove(Move move) {
        if (move.getThreefoldStatus() == Move.ThreefoldStatus.CLAIMED) {
            return GameStatus.threeFoldRepetition();
        }

        model.makeMove(move);
        moveStack.push(move);

        forEachPlayer(player -> player.updateByMove(move));
        return move.getMoveEvaluation().getGameStatus();
    }

    private boolean checkTimeOut() {
        return gameTime.getRunningTime(currentPlayer.getPlayerColor()).didRunOut();
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
        Arrays.stream(getPlayers()).parallel().forEach(callback::callback);
    }

    public Player[] getPlayers() {
        return new Player[]{gameCreator, p2};
    }

    void interruptRead(GameStatus status) {
        MyError err = new Game.GameOverError(status);
        if (isReadingMove) {
            currentPlayer.interrupt(err);
        } else throw err;

    }

    @Override
    public String toString() {
        return "Game{" +
                "p1=" + gameCreator.getUsername() +
                ", p2=" + p2.getUsername() +
                '}';
    }

    public void drawOffered(Player player) {
        session.log(player + " offered a draw");
        player.getPartner().drawOffered(ans -> {
            session.log(player.getPartner() + " responded with a " + ans + " to the draw offer");
        });
    }

    public static class PlayerDisconnectedError extends MyError.DisconnectedError {
        private final Player disconnectedPlayer;

        public PlayerDisconnectedError(Player disconnectedPlayer) {
            this.disconnectedPlayer = disconnectedPlayer;
        }

        public Player getDisconnectedPlayer() {
            return disconnectedPlayer;
        }
    }

    public static class GameOverError extends MyError {

        public final GameStatus gameOverStatus;

        public GameOverError(GameStatus gameOverStatus) {
            super(ErrorType.UnKnown);

            this.gameOverStatus = gameOverStatus;
        }
    }
}
