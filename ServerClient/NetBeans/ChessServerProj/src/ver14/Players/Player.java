package ver14.Players;

import ver14.Game.Game;
import ver14.Game.GameSession;
import ver14.SharedClasses.Callbacks.AnswerCallback;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;


/**
 * Player - represents a player capable of generating a selected move, respond to questions, and more.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class Player {
    /**
     * The Game.
     */
    protected Game game;
    /**
     * The Game session.
     */
    protected GameSession gameSession;
    /**
     * The Player color.
     */
    protected PlayerColor playerColor;
    /**
     * The Partner.
     */
    protected Player partner = null;
    /**
     * The Username.
     */
    private String username;
    /**
     * The Created game id.
     */
    private String createdGameID;

    /**
     * Instantiates a new Player.
     *
     * @param id the id
     */
    public Player(String id) {
        this.username = id;
    }

    /**
     * Gets game session.
     *
     * @return the game session
     */
    public GameSession getGameSession() {
        return gameSession;
    }

    /**
     * Sets game session.
     *
     * @param gameSession the game session
     */
    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    /**
     * Gets partner.
     *
     * @return the partner
     */
    public Player getPartner() {
        return partner;
    }

    /**
     * Sets partner.
     *
     * @param partner the partner
     */
    public void setPartner(Player partner) {
        this.partner = partner;
    }

    /**
     * Gets player color.
     *
     * @return the player color
     */
    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    /**
     * Sets player color.
     *
     * @param playerColor the player color
     */
    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Initialize a new game.
     *
     * @param game the game
     */
    public void initGame(Game game) {
        this.game = game;
        initGame();
    }

    /**
     * Init game.
     */
    protected abstract void initGame();

    /**
     * Gets created game id.
     *
     * @return the created game id
     */
    public String getCreatedGameID() {
        return createdGameID;
    }

    /**
     * Sets created game id.
     *
     * @param createdGameID the created game id
     */
    public void setCreatedGameID(String createdGameID) {
        this.createdGameID = createdGameID;
    }

    /**
     * alert the player of an Error.
     *
     * @param error the error
     */
    public abstract void error(String error);

    /**
     * ask the player to choose a move.
     *
     * @return the chosen move
     */
    public abstract Move getMove();

    /**
     * Wait for your opponent to make his turn.
     */
    public abstract void waitTurn();

    /**
     * notify player of a Game over.
     *
     * @param gameStatus the game over status
     */
    public abstract void gameOver(GameStatus gameStatus);


    /**
     * Ask player a question. like for example at the end of a game, the players are asked if they want to rematch
     *
     * @param question the question
     * @param onAns    the callback for answering
     */
    public void askQuestion(Question question, AnswerCallback onAns) {

        onAns.callback(question.getDefaultAnswer());
    }

    /**
     * notifies player of a change in the board. so he can Update his board.
     *
     * @param move the move
     */
    public abstract void updateByMove(Move move);

    /**
     * Cancel a previously asked question.
     *
     * @param question the question
     * @param cause    the cancellation cause
     */
    public abstract void cancelQuestion(Question question, String cause);

    /**
     * Interrupt a {@link #getMove()} with an error.
     *
     * @param error the error
     */
    public abstract void interrupt(MyError error);

    /**
     * Disconnect.
     *
     * @param cause             the cause
     * @param notifyGameSession the notify game session
     */
    public abstract void disconnect(String cause, boolean notifyGameSession);

    /**
     * Wait for match.
     */
    public abstract void waitForMatch();

    /**
     * ask player for his preferred game settings. games that are available for this player are passed to him.
     *
     * @param joinableGames  the joinable games available for this player to join
     * @param resumableGames the resumable games available for this player to resume playing
     * @return the game settings
     */
    public abstract GameSettings getGameSettings(SyncedItems<?> joinableGames, SyncedItems<?> resumableGames);

    /**
     * Resigned.
     */
    public void resigned() {
        assert gameSession != null;
        gameSession.resigned(this);
    }

    /**
     * Gets on going game.
     *
     * @return the on going game
     */
    public Game getOnGoingGame() {
        return game;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        if (playerColor != null) {
            return username + " " + playerColor;
        }
        return username;
    }

    /**
     * Is save worthy boolean.
     *
     * @return the boolean
     */
    public boolean isSaveWorthy() {
        return !isGuest() && !isAi();
    }

    /**
     * Is guest boolean.
     *
     * @return the boolean
     */
    public abstract boolean isGuest();

    /**
     * Is ai boolean.
     *
     * @return the boolean
     */
    public abstract boolean isAi();

    /**
     * Is connected boolean.
     *
     * @return the boolean
     */
    public boolean isConnected() {
        return true;
    }

    /**
     * Is playing boolean.
     *
     * @return the boolean
     */
    public boolean isPlaying() {
        return gameSession != null;
    }
}
