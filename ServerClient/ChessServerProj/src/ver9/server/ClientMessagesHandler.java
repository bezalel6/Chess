package ver9.server;

import ver9.SharedClasses.Callbacks.MessageCallback;
import ver9.SharedClasses.messages.Message;
import ver9.SharedClasses.networking.AppSocket;
import ver9.SharedClasses.networking.MessagesHandler;
import ver9.server.DB.DB;
import ver9.server.game.Game;
import ver9.server.game.GameSession;
import ver9.server.players.PlayerNet;

import java.util.ArrayList;

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

                GameSession gameSession = player.getGameSession();
                if (gameSession != null) {
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
