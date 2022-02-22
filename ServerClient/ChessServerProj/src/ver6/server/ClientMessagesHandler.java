package ver6.server;

import ver6.SharedClasses.PlayerStatistics;
import ver6.SharedClasses.messages.Message;
import ver6.SharedClasses.networking.AppSocket;
import ver6.SharedClasses.networking.MessagesHandler;
import ver6.SharedClasses.networking.MyErrors;
import ver6.server.DB.DB;
import ver6.server.players.PlayerNet;

public class ClientMessagesHandler extends MessagesHandler {
    private final Server server;
    private PlayerNet player = null;

    public ClientMessagesHandler(Server server, AppSocket appSocket) {
        super(appSocket);
        this.server = server;
    }

    public void setPlayer(PlayerNet playerNet) {
        this.player = playerNet;
    }

    @Override
    public void onResign(Message message) {
        super.onResign(message);
        player.getOnGoingGame().resigned(player);
    }

    @Override
    public void onUsernameAvailability(Message message) throws MyErrors {
        super.onUsernameAvailability(message);
        boolean res = !DB.isUsernameExists(message.getUsername());
        socket.writeMessage(Message.returnUsernameAvailable(res, message));
    }

    @Override
    public void onPlayersStatistics(Message message) throws MyErrors {
        super.onPlayersStatistics(message);
        PlayerStatistics stats = DB.getPlayersStatistics(player.getUsername());
        socket.writeMessage(Message.returnPlayersStatistics(stats, message));
    }

    @Override
    public void onDisconnected() {
        server.disconnectPlayer(player, " player disconnected");
        super.onDisconnected();
    }
}
