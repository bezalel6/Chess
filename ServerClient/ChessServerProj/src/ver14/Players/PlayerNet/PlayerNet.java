package ver14.Players.PlayerNet;

import ver14.DB.DB;
import ver14.Game.Game;
import ver14.Players.Player;
import ver14.SharedClasses.Callbacks.AnswerCallback;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.GameSetup.GameTime;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.Login.LoginType;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.Networking.AppSocket;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.SharedClasses.Networking.Messages.MessageType;
import ver14.SharedClasses.Sync.SyncableItem;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Sync.UserInfo;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;


/**
 * Player net - represents a player connected to a client through an {@link AppSocket}.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class PlayerNet extends Player implements SyncableItem {
    /**
     * The Login info.
     */
    private final LoginInfo loginInfo;
    /**
     * The Profile pic.
     */
    private final String profilePic;
    /**
     * The Socket to client.
     */
    private AppSocket socketToClient;

    /**
     * Instantiates a new Player net.
     *
     * @param socketToClient the socket to client
     * @param loginInfo      the login info
     */
    public PlayerNet(AppSocket socketToClient, LoginInfo loginInfo) {
        this(socketToClient, loginInfo, loginInfo.getUsername());
    }

    /**
     * Instantiates a new Player net.
     *
     * @param socketToClient the socket to client
     * @param loginInfo      the login info
     * @param id             the id
     */
    public PlayerNet(AppSocket socketToClient, LoginInfo loginInfo, String id) {
        super(id);
        this.loginInfo = loginInfo;
        this.socketToClient = socketToClient;
        this.profilePic = isGuest() ? null : DB.getProfilePic(id);
    }

    /**
     * Gets login info.
     *
     * @return the login info
     */
    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    /**
     * Init game.
     */
    @Override
    public void initGame() {
        getSocketToClient().writeMessage(Message.initGame(game.getModel().getLogicBoard(),
                getPartner().getUsername(),
                getPlayerColor(),
                game.getGameTime().clean(),
                game.getMoveStack()));
    }

    /**
     * Gets socket to client.
     *
     * @return the socket to client
     */
    public AppSocket getSocketToClient() {
        return socketToClient;
    }

    /**
     * Sets socket to client.
     *
     * @param socketToClient the socket to client
     */
    public void setSocketToClient(AppSocket socketToClient) {
        this.socketToClient = socketToClient;
    }

    /**
     * Error.
     *
     * @param error the error
     */
    @Override
    public void error(String error) {
        socketToClient.writeMessage(Message.error(error));
    }

    /**
     * Gets move.
     *
     * @return the move
     */
    @Override
    public Move getMove() {
        // with socket do...
        var moves = game.getMoves();
        GameTime gameTime = game.getGameTime().clean();
        Message moveMsg = socketToClient.requestMessage(Message.askForMove(moves, gameTime));
        if (moveMsg == null || moveMsg.getMessageType() == MessageType.INTERRUPT) {
            throw new Game.PlayerDisconnectedError(this);
        }
        assert moveMsg.getMessageType() == MessageType.GET_MOVE;
        return moveMsg.getMove();
    }

    /**
     * Wait turn.
     */
    @Override
    public void waitTurn() {
        socketToClient.writeMessage(Message.waitForYourTurn(getPartner().getUsername(), game.getGameTime().clean()));
    }

    /**
     * Game over.
     *
     * @param gameStatus the game status
     */
    @Override
    public void gameOver(GameStatus gameStatus) {
        socketToClient.writeMessage(Message.gameOver(gameStatus));
    }

    /**
     * Ask question.
     *
     * @param question the question
     * @param onAns    the on ans
     */
    @Override
    public void askQuestion(Question question, AnswerCallback onAns) {
        socketToClient.requestMessage(Message.askQuestion(question), msg -> onAns.callback(msg != null ? msg.getAnswer() : null));
    }

    /**
     * Update by move.
     *
     * @param move the move
     */
    @Override
    public void updateByMove(Move move) {
        socketToClient.writeMessage(Message.updateByMove(move, game.getGameTime().clean()));
    }

    /**
     * Cancel question.
     *
     * @param question the question
     * @param cause    the cause
     */
    @Override
    public void cancelQuestion(Question question, String cause) {
        socketToClient.writeMessage(Message.cancelQuestion(question, cause));
//        sendInterrupt();
//        socketToClient.writeMessage(Message.interrupt());
    }

    /**
     * Interrupt.
     *
     * @param error the error
     */
    @Override
    public void interrupt(MyError error) {
        socketToClient.interruptListener(error);
    }

    /**
     * Disconnect.
     *
     * @param cause             the cause
     * @param notifyGameSession the notify game session
     */
    @Override
    public void disconnect(String cause, boolean notifyGameSession) {
        if (socketToClient.isConnected()) {
            socketToClient.writeMessage(Message.bye(cause));
//                interrupt(new MyError.DisconnectedError(cause));
        }
        socketToClient.close();
        if (gameSession != null && notifyGameSession) {
            gameSession.playerDisconnected(this);
        }
    }

    /**
     * Wait for match.
     */
    @Override
    public void waitForMatch() {
        socketToClient.writeMessage(Message.waitForMatch());
    }

    /**
     * Gets game settings.
     *
     * @param joinableGames  the joinable games
     * @param resumableGames the resumable games
     * @return the game settings
     */
    public GameSettings getGameSettings(SyncedItems<?> joinableGames, SyncedItems<?> resumableGames) {
        Message response = socketToClient.requestMessage(Message.askForGameSettings(joinableGames, resumableGames));

        return response == null ? null : response.getGameSettings();
    }

    /**
     * Is guest boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isGuest() {
        return loginInfo.getLoginType() == LoginType.GUEST;
    }

    /**
     * Is ai boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isAi() {
        return false;
    }

    /**
     * Is connected boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isConnected() {
        return socketToClient.isConnected();
    }

    /**
     * Gets syncable item.
     *
     * @return the syncable item
     */
    @Override
    public SyncableItem getSyncableItem() {
        return new UserInfo(ID(), profilePic);
    }

    /**
     * Id string.
     *
     * @return the string
     */
    @Override
    public String ID() {
        return getUsername();
    }
}
