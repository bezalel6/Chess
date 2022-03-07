package ver14.game;

import ver14.Model.Model;
import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.GameSettings;
import ver14.SharedClasses.GameTime;
import ver14.SharedClasses.PlayerColor;
import ver14.SharedClasses.SavedGames.EstablishedGameInfo;
import ver14.SharedClasses.SavedGames.UnfinishedGame;
import ver14.SharedClasses.evaluation.GameStatus;
import ver14.SharedClasses.moves.Move;
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
    private final GameView gameView;
    private final Player gameCreator, p2;
    private final Model model;
    private final GameSettings originalSettings;
    private Stack<Move> moveStack;
    private GameSettings gameSettings;
    private GameTime gameTime;
    private Player currentPlayer;
    private Player resignedPlayer;
    private Player disconnectedPlayer;
    private boolean isReadingMove = false;
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
        disconnectedPlayer = null;
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
        assignColors();
        setCurrentPlayer();
        gameTime = new GameTime(gameSettings.getTimeFormat());
        initPlayersGames();
        session.log("Starting game " + this);
        gameView.update(model.getLogicBoard());
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

    private GameStatus playTurn() {
//        gameTime.startRunning(currentPlayer.getPlayerColor());
        Move move = getMove();
//        gameTime.stopRunning(currentPlayer.getPlayerColor());
//        if (checkTimeOut()) {
//            return GameStatus.timedOut(currentPlayer.getPlayerColor());
//        }
        return makeMove(move);
    }

    private void switchTurn() {
        gameView.update(model.getLogicBoard());
        currentPlayer = currentPlayer.getPartner();
        gameTime.startRunning(currentPlayer.getPlayerColor());
        currentPlayer.getPartner().waitTurn();
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
        isReadingMove = true;
        Move move = currentPlayer.getMove();
        isReadingMove = false;
        if (move == null || disconnectedPlayer != null) {
            if (disconnectedPlayer == null)
                disconnectedPlayer = currentPlayer;
            return null;
        }
        session.log("move(%d) from %s: %s".formatted(moveStack.size() + 1, currentPlayer, move));//+1 bc current one isnt pushed yet

//        move.getMoveAnnotation().resetPromotion(move);
        move.setMovingColor(currentPlayer.getPlayerColor());
        return move;
    }

    private GameStatus makeMove(Move move) {
        if (resignedPlayer != null) {
            return GameStatus.playerResigned(resignedPlayer.getPlayerColor());
        }
        if (disconnectedPlayer != null) {
            return GameStatus.playerDisconnected(disconnectedPlayer.getPlayerColor());
        }
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

    public Player getResignedPlayer() {
        return resignedPlayer;
    }

    public Player getDisconnectedPlayer() {
        return disconnectedPlayer;
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


    @Override
    public String toString() {
        return "Game{" +
                "p1=" + gameCreator +
                ", p2=" + p2 +
                ", gameSettings=" + gameSettings +
                '}';
    }


    public void drawOffered(Player player) {
        session.log(player + " offered a draw");
        player.getPartner().drawOffered(ans -> {
            session.log(player.getPartner() + " responded with a " + ans + " to the draw offer");
        });
    }
}
