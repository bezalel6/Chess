package ver11.Server;

import ver11.Server.DB.DB;
import ver11.Server.game.Game;
import ver11.Server.game.GameSession;
import ver11.Server.players.PlayerNet;
import ver11.SharedClasses.Callbacks.MessageCallback;
import ver11.SharedClasses.messages.Message;
import ver11.SharedClasses.networking.AppSocket;
import ver11.SharedClasses.networking.MessagesHandler;

import java.util.ArrayList;

public class ServerMessagesHandler extends MessagesHandler {
    private final Server server;
    private PlayerNet player = null;

    public ServerMessagesHandler(Server server, AppSocket appSocket) {
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
                    game.drawOffered(player);
                }
            }
        };
    }

    @Override
    public MessageCallback onBye() {
        return message -> {
            super.onBye().onMsg(message);

            String response = "bye";
            if (player != null) {
                response += " " + player.getUsername();

                GameSession gameSession = player.getGameSession();
                if (gameSession != null) {
                    //todo smn
                    Game game = player.getOnGoingGame();
                    if (game != null) {
//                        response += " saving game";
                        game.playerDisconnected(player);
                    }
                }
            }
            socket.respond(Message.bye(response), message);
        };
    }

    @Override
    public MessageCallback onUsernameAvailability() {
        return message -> {
            super.onUsernameAvailability().onMsg(message);

            boolean res = !DB.isUsernameExists(message.getUsername());
            if (res) {
                socket.writeMessage(Message.returnUsernameAvailable(message));
            } else {
                ArrayList<String> suggestions = server.createUsernameSuggestions(message.getUsername());
                socket.writeMessage(Message.returnUsernameNotAvailable(suggestions, message));
            }

        };
    }

    @Override
    public MessageCallback onDBRequest() {
        return message -> {
            super.onDBRequest().onMsg(message);
            socket.writeMessage(Message.returnDBResponse(DB.request(message.getDBRequest()), message));
        };
    }
}
