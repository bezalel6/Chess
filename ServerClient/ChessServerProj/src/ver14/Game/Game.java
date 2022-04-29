package ver14.Game;

import ver14.Model.Eval.Eval;
import ver14.Model.Model;
import ver14.Model.MoveGenerator.GenerationSettings;
import ver14.Model.MoveGenerator.MoveGenerator;
import ver14.Players.Player;
import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.GameSetup.GameTime;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.Moves.MovesList;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.SavedGames.EstablishedGameInfo;
import ver14.SharedClasses.Game.SavedGames.UnfinishedGame;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.UI.GameView;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Game - represents a game between two {@link Player}s.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Game {
    /**
     * The constant ROWS.
     */
    public static final int ROWS = 8;
    /**
     * The constant COLS.
     */
    public static final int COLS = 8;
    /**
     * The constant showGameView.
     */
    public static boolean showGameView = false;
    /**
     * The Session.
     */
    private final GameSession session;
    /**
     * The Game creator.
     */
    private final Player gameCreator;
    /**
     * The P 2.
     */
    private final Player p2;
    /**
     * The Original settings.
     */
    private final GameSettings originalSettings;
    /**
     * The Game view.
     */
    private final GameView gameView;
    /**
     * The Model.
     */
    private Model model;
    /**
     * The Move stack.
     */
    private Stack<Move> moveStack;
    /**
     * The Game settings.
     */
    private GameSettings gameSettings;
    /**
     * The Game time.
     */
    private GameTime gameTime;
    /**
     * The Current player.
     */
    private Player currentPlayer;
    /**
     * The Is reading move.
     */
    private boolean isReadingMove = false;
    /**
     * The Creator color.
     */
    private PlayerColor creatorColor = null;
    /**
     * The Clear move stack.
     */
    private boolean clearMoveStack = true;
    /**
     * The Throw err.
     */
    private GameOverError throwErr = null;

    /**
     * Instantiates a new Game.
     *
     * @param gameCreator  the game creator
     * @param p2           the p 2
     * @param gameSettings the game settings
     * @param session      the session
     */
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

    /**
     * Sets partners.
     */
    private void setPartners() {
        gameCreator.setPartner(p2);
        p2.setPartner(gameCreator);
    }

    /**
     * Gets current game state.
     *
     * @return the current game state
     */
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

    /**
     * Gets game settings.
     *
     * @return the game settings
     */
    public GameSettings getGameSettings() {
        return gameSettings;
    }

    /**
     * Gets game creator.
     *
     * @return the game creator
     */
    public Player getGameCreator() {
        return gameCreator;
    }

    /**
     * Gets game time.
     *
     * @return the game time
     */
    public GameTime getGameTime() {
        return gameTime;
    }

    /**
     * Gets model.
     *
     * @return the model
     */
    public Model getModel() {
        return model;
    }

    /**
     * Start a new game.
     *
     * @return the game over status
     */
    public GameStatus startNewGame() {
        this.moveStack = new Stack<>();
        this.model = new Model();
        initGame();
        return runGame();
    }

    /**
     * Initializes a game.
     */
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

    /**
     * Run game game status.
     *
     * @return the game status
     */
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
        session.log("game over. " + gameOverStatus.getDetailedStr());
        return gameOverStatus;
    }

    /**
     * Assign colors.
     */
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

    /**
     * Sets current player.
     */
    private void setCurrentPlayer() {
        currentPlayer = getCurrentPlayer();
    }

    /**
     * Init players games.
     */
    private void initPlayersGames() {
        forEachPlayer(p -> p.initGame(this));
    }

    /**
     * Updates debug view.
     */
    private void updateDebugView() {
        if (this.gameView != null) {
            this.gameView.update(model.getLogicBoard());
        }
    }

    /**
     * gets the current player's move, makes it, and returns the game status after making the move.
     * might stop and not finish all those steps if it gets interrupted by a disconnected player or a the current player timing out.
     * in which case the appropriate game status will be returned.
     *
     * @return the game status
     */
    private GameStatus playTurn() {
        currentPlayer.getPartner().waitTurn();
        try {
            Move move = getMove();
            GameStatus status = makeMove(move);
            checkStatus();
            return status;
        } catch (GameOverError error) {
            return error.gameOverStatus;
        }
    }

    /**
     * Switches turn.
     */
    private void switchTurn() {
        currentPlayer = currentPlayer.getPartner();
        updateDebugView();
    }

    /**
     * On game over.
     */
    private void onGameOver() {
        gameSettings.setPlayerToMove(currentPlayer.getPlayerColor());
        gameSettings.setFen(model.genFenStr());
    }

    /**
     * Gets current player.
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return (gameCreator.getPlayerColor() == model.getCurrentPlayer() ? gameCreator : p2);
    }

    /**
     * For each player.
     *
     * @param callback the callback
     */
    public void forEachPlayer(Callback<Player> callback) {
        callback.callback(gameCreator);
        callback.callback(p2);
    }

    /**
     * Gets move.
     *
     * @return the move
     */
    private Move getMove() {
        ExecutorService gameTimeExecutor = Executors.newSingleThreadExecutor();
        try {
            gameTime.startRunning(currentPlayer.getPlayerColor());
            gameTimeExecutor.submit(() -> {
                try {
                    Thread.sleep(gameTime.getTimeLeft(currentPlayer.getPlayerColor()));
                    if (!gameTimeExecutor.isShutdown())
                        interrupt(GameStatus.timedOut(currentPlayer.getPlayerColor(), Eval.isSufficientMaterial(currentPlayer.getPlayerColor().getOpponent(), model)));
                } catch (InterruptedException e) {
                }
            });
            checkStatus();
            isReadingMove = true;
            Move move = currentPlayer.getMove();
            isReadingMove = false;

            Move finalMove = move;
            move = getMoves().stream().filter(m -> m.strictEquals(finalMove)).findAny().orElse(null);

            session.log("move(%d) from %s: %s".formatted(moveStack.size() + 1, currentPlayer, move));//+1 bc current one isnt pushed yet

            return move;
        } catch (PlayerDisconnectedError error) {
            throw new GameOverError(error.createGameStatus());
        } finally {
            isReadingMove = false;
            gameTimeExecutor.shutdown();
            gameTimeExecutor.shutdownNow();
        }
    }

    /**
     * Make move game status.
     *
     * @param move the move
     * @return the game status
     */
    private GameStatus makeMove(Move move) {
        if (move.getThreefoldStatus() == Move.ThreefoldStatus.CLAIMED) {
            return GameStatus.threeFoldRepetition();
        }

        model.makeMove(move);
        moveStack.push(move);

        session.log(model.getMoveStack().toString());

        currentPlayer.getPartner().updateByMove(move);

        return move.getMoveEvaluation().getGameStatus();
    }

    /**
     * Check status.
     *
     * @throws GameOverError the game over error
     */
    private void checkStatus() throws GameOverError {
        if (throwErr != null) {
            throw throwErr;
        }
    }

    /**
     * Interrupt.
     *
     * @param gameOverStatus the game over status
     */
    void interrupt(GameStatus gameOverStatus) {
        GameOverError err = new Game.GameOverError(gameOverStatus);
        if (isReadingMove) {
            currentPlayer.interrupt(err);
        } else throwErr = err;

    }

    /**
     * Gets moves.
     *
     * @return the moves
     */
    public MovesList getMoves() {
        return MoveGenerator.generateMoves(model, GenerationSettings.ANNOTATE).getCleanList();
    }

    /**
     * Check time out boolean.
     *
     * @return the boolean
     */
    private boolean checkTimeOut() {
        return gameTime.getRunningTime(currentPlayer.getPlayerColor()).didRunOut();
    }

    /**
     * Gets creator color.
     *
     * @return the creator color
     */
    public PlayerColor getCreatorColor() {
        return creatorColor;
    }

    /**
     * Sets creator color.
     *
     * @param creatorColor the creator color
     */
    public void setCreatorColor(PlayerColor creatorColor) {
        this.creatorColor = creatorColor;
    }

    /**
     * Gets move stack.
     *
     * @return the move stack
     */
    public Stack<Move> getMoveStack() {
        return moveStack;
    }

    /**
     * Sets move stack.
     *
     * @param moveStack the move stack
     */
    public void setMoveStack(Stack<Move> moveStack) {
        this.moveStack = moveStack;
        clearMoveStack = false;
    }

    /**
     * Parallel for each player.
     *
     * @param callback the callback
     */
    public void parallelForEachPlayer(Callback<Player> callback) {
        Arrays.stream(getPlayers()).parallel().forEach(callback::callback);
    }


    /**
     * Get players player [ ].
     *
     * @return the player [ ]
     */
    public Player[] getPlayers() {
        return new Player[]{gameCreator, p2};
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "%s vs %s".formatted(gameCreator.getUsername(), p2.getUsername());
    }


    /**
     * Player disconnected error.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class PlayerDisconnectedError extends MyError.DisconnectedError {
        /**
         * The Disconnected player.
         */
        private final Player disconnectedPlayer;

        /**
         * Instantiates a new Player disconnected error.
         *
         * @param disconnectedPlayer the disconnected player
         */
        public PlayerDisconnectedError(Player disconnectedPlayer) {
            this.disconnectedPlayer = disconnectedPlayer;
        }

        /**
         * Create game status game status.
         *
         * @return the game status
         */
        public GameStatus createGameStatus() {
            return GameStatus.playerDisconnected(disconnectedPlayer.getPlayerColor(), disconnectedPlayer.getPartner().isAi());
        }

        /**
         * Gets disconnected player.
         *
         * @return the disconnected player
         */
        public Player getDisconnectedPlayer() {
            return disconnectedPlayer;
        }
    }

    /**
     * Game over error.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
//    todo change to throwable
    public static class GameOverError extends MyError {

        /**
         * The Game over status.
         */
        public final GameStatus gameOverStatus;

        /**
         * Instantiates a new Game over error.
         *
         * @param gameOverStatus the game over status
         */
        public GameOverError(GameStatus gameOverStatus) {
            this.gameOverStatus = gameOverStatus;
        }
    }

}
