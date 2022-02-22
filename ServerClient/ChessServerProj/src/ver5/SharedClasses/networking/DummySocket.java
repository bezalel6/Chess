package ver5.SharedClasses.networking;

import ver5.SharedClasses.GameSettings;
import ver5.SharedClasses.LoginInfo;
import ver5.SharedClasses.SavedGame;
import ver5.SharedClasses.messages.Message;
import ver5.SharedClasses.messages.MessageCallback;

import java.util.ArrayList;
import java.util.Collection;

public class DummySocket extends AppSocket {
    private final boolean username = true;
    private Message respond = null;

    public DummySocket() {
    }

    @Override
    public void sendIsAlive() {
        super.sendIsAlive();
    }

    @Override
    public void writeMessage(Message msg) {
        respond = switch (msg.getMessageType()) {
            case LOGIN -> Message.welcomeMessage("welcome bish", new LoginInfo());
            case USERNAME_AVAILABILITY -> Message.returnUsernameAvailable(username);
            case RESUMABLE_GAMES, JOINABLE_GAMES -> Message.returnResumableGames(createDummyGames(10));
            default -> null;
        };
    }

    public static Collection<SavedGame> createDummyGames(int num) {
        ArrayList<SavedGame> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            list.add(new SavedGame("id" + i, "username", GameSettings.EXAMPLE));
        }

        return list;
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public AppSocket getBackgroundSocket() {
        return this;
    }

    @Override
    public void readMessage(MessageCallback onMsg) {
        onMsg.callback(readMessage());
    }

    @Override
    public Message readMessage() {
        return respond;
    }
}
