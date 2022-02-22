package ver6.server.players;

import ver6.SharedClasses.*;
import ver6.SharedClasses.Sync.SyncableItem;
import ver6.SharedClasses.Sync.SyncedItems;
import ver6.SharedClasses.evaluation.GameStatus;
import ver6.SharedClasses.messages.Message;
import ver6.SharedClasses.messages.MessageType;
import ver6.SharedClasses.moves.Move;
import ver6.SharedClasses.networking.AppSocket;
import ver6.SharedClasses.networking.MyErrors;
import ver6.server.game.Game;

import java.util.ArrayList;

/**
 * PlayerNet.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public class PlayerNet extends Player implements SyncableItem {
    public final LoginInfo loginInfo;
    private AppSocket socketToClient;

    public PlayerNet(AppSocket socketToClient, LoginInfo loginInfo, String id) {
        super(id);
        this.loginInfo = loginInfo;
        this.socketToClient = socketToClient;
    }

    @Override
    public void initGame() {
        getSocketToClient().writeMessage(Message.initGame(game.getModel().getLogicBoard(),
                getPartner().toString(),
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
    public Move getMove() throws MyErrors {
        // with socket do...
        ArrayList<Move> moves = game.getModel().generateAllMoves(playerColor);
        GameTime gameTime = game.getGameTime().clean();
        Message moveMsg = socketToClient.requestMessage(Message.askForMove(moves, gameTime));
        if (moveMsg == null || moveMsg.getMessageType() == MessageType.INTERRUPT) {
            return null;
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
    public boolean askForRematch() throws MyErrors {
        Message msg = socketToClient.requestMessage(Message.askQuestion(Question.Rematch));
        return msg != null && msg.getMessageType() != MessageType.INTERRUPT && msg.getQuestion().getAnswer();
    }

    @Override
    public void updateByMove(Move move) {
        socketToClient.writeMessage(Message.updateByMove(move, game.getGameTime().clean()));
    }

    @Override
    public void cancelRematch() {
        interrupt();
    }

    @Override
    public void interrupt() {
        socketToClient.interruptListener();
    }

    @Override
    public void disconnect(String cause) {
        Game game = getOnGoingGame();
        if (game != null) {
            game.playerDisconnected(this);
        }
        interrupt();
        socketToClient.writeMessage(Message.bye(cause));
        socketToClient.close();
    }

    @Override
    public void waitForMatch() {
        socketToClient.writeMessage(Message.waitForMatch());
    }

    public GameSettings getGameSettings(SyncedItems joinableGames, SyncedItems resumableGames) throws MyErrors {

        Message response = socketToClient.requestMessage(Message.askForGameSettings(joinableGames, resumableGames));

        return response == null ? null : response.getGameSettings();
    }

    @Override
    public boolean isGuest() {
        return loginInfo.getLoginType() == LoginType.GUEST;
    }

    @Override
    public String ID() {
        return getUsername();
    }
}
