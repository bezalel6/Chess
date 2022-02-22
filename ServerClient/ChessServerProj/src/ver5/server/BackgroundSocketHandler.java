package ver5.server;

import ver5.SharedClasses.NoThrow;
import ver5.SharedClasses.messages.Message;
import ver5.SharedClasses.networking.AppSocket;
import ver5.server.DB.DB;
import ver5.server.players.PlayerNet;

public class BackgroundSocketHandler extends Thread {
    private final AppSocket backgroundSocket;
    private final Server server;
    private PlayerNet player = null;

    public BackgroundSocketHandler(AppSocket backgroundSocket, Server server) {
        this.backgroundSocket = backgroundSocket;
        this.server = server;
    }

    public void setPlayer(PlayerNet playerNet) {
        this.player = playerNet;
    }

    @Override
    public void run() {
        while (backgroundSocket.isConnected()) {
            Message msg = backgroundSocket.readMessage();
            if (msg == null)
                break;

            Message response = switch (msg.getMessageType()) {
                case USERNAME_AVAILABILITY -> Message.returnUsernameAvailable(new NoThrow<>(() -> !DB.isUsernameExists(msg.getUsername())).get());
                case JOINABLE_GAMES -> Message.returnJoinableGames(server.getJoinableGames(player));
                case RESUMABLE_GAMES -> Message.returnResumableGames(server.getResumableGames(player));
                case PLAYERS_STATISTICS -> Message.returnPlayersStatistics(new NoThrow<>(() -> DB.getPlayersStatistics(player.getUsername())).get());
                case RESIGN -> {
                    player.getOnGoingGame().resigned(player);
                    yield (null);
                }
                default -> null;
            };
            if (response != null)
                backgroundSocket.writeMessage(response);
        }
    }


}
