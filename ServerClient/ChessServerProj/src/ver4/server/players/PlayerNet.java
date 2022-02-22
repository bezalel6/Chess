package ver4.server.players;

import ver4.SharedClasses.evaluation.GameStatus;
import ver4.SharedClasses.messages.Message;
import ver4.SharedClasses.messages.MessageType;
import ver4.SharedClasses.moves.Move;
import ver4.SharedClasses.networking.AppSocket;

/**
 * PlayerNet.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public class PlayerNet extends Player {
    private AppSocket socketToClient;

    public PlayerNet(AppSocket socketToClient, String id) {
        super(id);
        this.socketToClient = socketToClient;
    }

    public AppSocket getSocketToClient() {
        return socketToClient;
    }

    public void setSocketToClient(AppSocket socketToClient) {
        this.socketToClient = socketToClient;
    }

    @Override
    public Move getMove() {
        // with socket do...
        socketToClient.writeMessage(Message.askForMove(model.generateAllMoves(playerColor)));
        Message moveMsg = socketToClient.readMessage();
        if (moveMsg == null) {
            return null;
        }
        assert moveMsg.getMessageType() == MessageType.GET_MOVE;
        return moveMsg.getMove();
    }

    @Override
    public boolean askForRematch() {
        socketToClient.writeMessage(Message.rematch());
        Message msg = socketToClient.readMessage();
        return msg != null && !msg.ignore() && msg.getRematch();
    }

    @Override
    public void disconnect(String cause) {
        socketToClient.writeMessage(Message.bye(cause));
        socketToClient.close();
    }

    @Override
    public void waitTurn() {
        socketToClient.writeMessage(Message.waitForYourTurn(playerColor.getOpponent()));
    }

    @Override
    public void gameOver(GameStatus gameStatus) {
        socketToClient.writeMessage(Message.gameOver(gameStatus));
    }
}
