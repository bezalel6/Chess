package ver14.players.PlayerNet;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Callbacks.QuestionCallback;
import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.GameTime;
import ver14.SharedClasses.Game.evaluation.GameStatus;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.LoginType;
import ver14.SharedClasses.Question;
import ver14.SharedClasses.Sync.SyncableItem;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Sync.UserInfo;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorHandler;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.messages.Message;
import ver14.SharedClasses.messages.MessageType;
import ver14.SharedClasses.networking.AppSocket;
import ver14.game.Game;
import ver14.players.Player;

import java.util.ArrayList;

/**
 * PlayerNet.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public class PlayerNet extends Player implements SyncableItem {
    private final LoginInfo loginInfo;
    private AppSocket socketToClient;

    public PlayerNet(AppSocket socketToClient, LoginInfo loginInfo) {
        this(socketToClient, loginInfo, loginInfo.getUsername());
    }

    public PlayerNet(AppSocket socketToClient, LoginInfo loginInfo, String id) {
        super(id);
        this.loginInfo = loginInfo;
        this.socketToClient = socketToClient;
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
        ArrayList<Move> moves = game.getModel().generateAllMoves(playerColor).getCleanList();
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
    public void askQuestion(Question question, QuestionCallback onAns) {
        socketToClient.requestMessage(Message.askQuestion(question), msg -> onAns.callback(msg.getAnswer()));
    }

    @Override
    public void updateByMove(Move move) {
        socketToClient.writeMessage(Message.updateByMove(move, game.getGameTime().clean()));
    }

    @Override
    public void cancelQuestion(Question question, String cause) {
//        sendInterrupt();
        socketToClient.writeMessage(Message.interrupt());
    }

    @Override
    public void interrupt(MyError error) {
        socketToClient.interruptListener(error);
    }

    @Override
    public void
    disconnect(String cause) {
        ErrorHandler.ignore(() -> {
            if (socketToClient.isConnected()) {
                socketToClient.writeMessage(Message.bye(cause));
                if (gameSession != null) {
                    gameSession.playerDisconnected(this);
                }
//                interrupt(new MyError.DisconnectedError(cause));
                socketToClient.close();
            }
        });
    }

    @Override
    public void waitForMatch() {
        socketToClient.writeMessage(Message.waitForMatch());
    }

    @Override
    public void drawOffered(Callback<Question.Answer> answerCallback) {
        socketToClient.requestMessage(Message.askQuestion(Question.drawOffer(getPartner().getUsername())), res -> answerCallback.callback(res.getQuestion().getAnswer()));
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
        return new UserInfo(ID());
    }

    @Override
    public String ID() {
        return getUsername();
    }
}