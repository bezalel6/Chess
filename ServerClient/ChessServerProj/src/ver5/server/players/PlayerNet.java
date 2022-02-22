package ver5.server.players;

import ver5.SharedClasses.GameSettings;
import ver5.SharedClasses.Question;
import ver5.SharedClasses.SavedGame;
import ver5.SharedClasses.evaluation.GameStatus;
import ver5.SharedClasses.messages.Message;
import ver5.SharedClasses.messages.MessageType;
import ver5.SharedClasses.moves.Move;
import ver5.SharedClasses.networking.AppSocket;

/**
 * PlayerNet.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public class PlayerNet extends Player {
    public final boolean isGuest;
    private AppSocket socketToClient;

    public PlayerNet(AppSocket socketToClient, boolean isGuest, String id) {
        super(id);
        this.isGuest = isGuest;
        this.socketToClient = socketToClient;
    }

    @Override
    public void initGame() {
        getSocketToClient().writeMessage(Message.initGame(game.getModel().getLogicBoard(), getPartner().toString(), getPlayerColor(), game.getGameTime().clean()));
    }

    public AppSocket getSocketToClient() {
        return socketToClient;
    }

    public void setSocketToClient(AppSocket socketToClient) {
        this.socketToClient = socketToClient;
    }

    @Override
    public boolean isGuest() {
        return isGuest;
    }

    @Override
    public Move getMove() {
        // with socket do...
        socketToClient.writeMessage(Message.askForMove(game.getModel().generateAllMoves(playerColor), game.getGameTime().clean()));
        Message moveMsg = socketToClient.readMessage();
        if (moveMsg == null) {
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
    public boolean askForRematch() {
        socketToClient.writeMessage(Message.question(Question.Rematch));
        Message msg = socketToClient.readMessage();
        return msg != null && !msg.ignore() && msg.getQuestion().getAnswer();
    }

    @Override
    public void updateByMove(Move move) {
        socketToClient.writeMessage(Message.makeMove(move, game.getGameTime().clean()));
    }

    @Override
    public void cancelRematch() {
        socketToClient.interruptListener();
    }

    @Override
    public void disconnect(String cause) {
        socketToClient.writeMessage(Message.bye(cause));
        socketToClient.close();
    }

    @Override
    public void waitForMatch() {
        socketToClient.writeMessage(Message.waitForMatch());
    }

    public GameSettings getGameSettings(SavedGame[] joinableGames, SavedGame[] resumableGames) {
        socketToClient.writeMessage(Message.askForGameSettings(joinableGames, resumableGames));
        Message response = socketToClient.readMessage();
        return response == null ? null : response.getGameSettings();
    }
}
