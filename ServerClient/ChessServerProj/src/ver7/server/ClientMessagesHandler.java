package ver7.server;

import ver7.SharedClasses.Callbacks.MessageCallback;
import ver7.SharedClasses.PlayerStatistics;
import ver7.SharedClasses.messages.Message;
import ver7.SharedClasses.networking.AppSocket;
import ver7.SharedClasses.networking.MessagesHandler;
import ver7.SharedClasses.networking.MyErrors;
import ver7.server.DB.DB;
import ver7.server.game.Game;
import ver7.server.players.PlayerNet;

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
    public void onDisconnected() {
        server.disconnectPlayer(player, " player disconnected");
        super.onDisconnected();
    }

    @Override
    public MessageCallback onResign() {
        return message -> {
            super.onResign().onMsg(message);
            player.getOnGoingGame().resigned(player);
        };
    }

    @Override
    public MessageCallback onOfferDraw() {
        return message -> {
            super.onOfferDraw().onMsg(message);
            if (player != null) {
                Game game = player.getOnGoingGame();
                if (game != null) {

                }
            }
        };
    }

    @Override
    public MessageCallback onBye() {
        return message -> {
            super.onBye().onMsg(message);

            String response = "bye!";
            if (player != null) {
                response += " " + player.getUsername();
                Game game = player.getOnGoingGame();
                if (game != null) {
                    response += " saving game";
                    game.playerDisconnected(player);
                }
            }
            socket.respond(Message.bye(response), message);
        };
    }

    @Override
    public MessageCallback onUsernameAvailability() {
        return message -> {
            super.onUsernameAvailability().onMsg(message);
            boolean res = false;
            try {
                res = !DB.isUsernameExists(message.getUsername());
            } catch (MyErrors e) {
                e.printStackTrace();
            }
            socket.writeMessage(Message.returnUsernameAvailable(res, message));
        };
    }

    @Override
    public MessageCallback onPlayersStatistics() {
        return message -> {
            super.onPlayersStatistics().onMsg(message);
            PlayerStatistics stats = null;
            try {
                stats = DB.getPlayersStatistics(player.getUsername());
            } catch (MyErrors e) {
                e.printStackTrace();
            }
            socket.writeMessage(Message.returnPlayersStatistics(stats, message));
        };
    }
}