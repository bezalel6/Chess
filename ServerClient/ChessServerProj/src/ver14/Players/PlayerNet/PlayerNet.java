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

/*
 * PlayerNet
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * PlayerNet -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * PlayerNet -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

/**
 * PlayerNet.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public class PlayerNet extends Player implements SyncableItem {
    private final LoginInfo loginInfo;
    private final String profilePic;
    private AppSocket socketToClient;

    public PlayerNet(AppSocket socketToClient, LoginInfo loginInfo) {
        this(socketToClient, loginInfo, loginInfo.getUsername());
    }

    public PlayerNet(AppSocket socketToClient, LoginInfo loginInfo, String id) {
        super(id);
        this.loginInfo = loginInfo;
        this.socketToClient = socketToClient;
        this.profilePic = isGuest() ? null : DB.getProfilePic(id);
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    @Override
    public void initGame() {
        getSocketToClient().writeMessage(Message.initGame(game.getModel().getLogicBoard(),
                getPartner().getUsername(),
                getPlayerColor(),
                game.getGameTime().clean(),
                game.getMoveStack()));
    }

    public AppSocket getSocketToClient() {
        return socketToClient;
    }

    public void setSocketToClient(AppSocket socketToClient) {
        this.socketToClient = socketToClient;
    }

    @Override
    public void error(String error) {
        socketToClient.writeMessage(Message.error(error));
    }

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

    @Override
    public void waitTurn() {
        socketToClient.writeMessage(Message.waitForYourTurn(getPartner().getUsername(), game.getGameTime().clean()));
    }

    @Override
    public void gameOver(GameStatus gameStatus) {
        socketToClient.writeMessage(Message.gameOver(gameStatus));
    }

    @Override
    public void askQuestion(Question question, AnswerCallback onAns) {
        socketToClient.requestMessage(Message.askQuestion(question), msg -> onAns.callback(msg.getAnswer()));
    }

    @Override
    public void updateByMove(Move move) {
        socketToClient.writeMessage(Message.updateByMove(move, game.getGameTime().clean()));
    }

    @Override
    public void cancelQuestion(Question question, String cause) {
        socketToClient.writeMessage(Message.cancelQuestion(question, cause));
//        sendInterrupt();
//        socketToClient.writeMessage(Message.interrupt());
    }

    @Override
    public void interrupt(MyError error) {
        socketToClient.interruptListener(error);
    }

    @Override
    public void disconnect(String cause) {
        if (socketToClient.isConnected()) {
            socketToClient.writeMessage(Message.bye(cause));
//                interrupt(new MyError.DisconnectedError(cause));
        }
        socketToClient.close();
        if (gameSession != null) {
            gameSession.playerDisconnected(this);
        }
    }

    @Override
    public void waitForMatch() {
        socketToClient.writeMessage(Message.waitForMatch());
    }

    public GameSettings getGameSettings(SyncedItems<?> joinableGames, SyncedItems<?> resumableGames) {
        Message response = socketToClient.requestMessage(Message.askForGameSettings(joinableGames, resumableGames));

        return response == null ? null : response.getGameSettings();
    }

    @Override
    public boolean isGuest() {
        return loginInfo.getLoginType() == LoginType.GUEST;
    }

    @Override
    public boolean isAi() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return socketToClient.isConnected();
    }

    @Override
    public SyncableItem getSyncableItem() {
        return new UserInfo(ID(), profilePic);
    }

    @Override
    public String ID() {
        return getUsername();
    }
}
