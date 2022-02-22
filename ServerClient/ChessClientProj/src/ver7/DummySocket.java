package ver7;

import ver7.SharedClasses.LoginInfo;
import ver7.SharedClasses.SavedGames.GameInfo;
import ver7.SharedClasses.messages.Message;
import ver7.SharedClasses.networking.AppSocket;

import java.util.ArrayList;
import java.util.Collection;

public class DummySocket extends AppSocket {
    private final boolean username = true;
    private Message respond = null;

    public DummySocket() {
    }

    public static Collection<GameInfo> createDummyGames(int num) {
        ArrayList<GameInfo> list = new ArrayList<>();
//        for (int i = 0; i < num; i++) {
//            list.add(new GameInfo("id" + i, "username", GameSettings.EXAMPLE));
//        }

        return list;
    }

    @Override
    public void writeMessage(Message msg) {
        respond = switch (msg.getMessageType()) {
            case LOGIN -> Message.welcomeMessage("welcome bish", new LoginInfo());
            case USERNAME_AVAILABILITY -> Message.returnUsernameAvailable(username, msg);
//            case RESUMABLE_GAMES, JOINABLE_GAMES -> Message.returnResumableGames(createDummyGames(10), msg);
            default -> null;
        };
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public boolean isConnected() {
        return true;
    }
}
