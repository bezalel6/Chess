package ver14;

import ver14.DB.DB;
import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.messages.Message;
import ver14.SharedClasses.networking.AppSocket;
import ver14.SharedClasses.networking.MessagesHandler;
import ver14.game.Game;
import ver14.players.PlayerNet.PlayerNet;

import java.util.ArrayList;

/**
 * The type Server messages handler.
 */
public class ServerMessagesHandler extends MessagesHandler {
    private final Server server;
    private PlayerNet player = null;

    /**
     * Instantiates a new Server messages handler.
     *
     * @param server    the server
     * @param appSocket the app socket
     */
    public ServerMessagesHandler(Server server, AppSocket appSocket) {
        super(appSocket);
        this.server = server;
    }

    /**
     * Sets player.
     *
     * @param playerNet the player net
     */
    public void setPlayer(PlayerNet playerNet) {
        this.player = playerNet;
    }

    @Override
    public MessageCallback onCancelQuestion() {
        return message -> {
            super.onCancelQuestion().onMsg(message);
            player.getPartner().cancelQuestion(message.getQuestion(), message.getCancelingQuestionCause());
        };
    }

    @Override
    protected void onAnyDisconnection() {
        server.playerDisconnected(player, "");
        super.onAnyDisconnection();
    }

    @Override
    protected MyError.DisconnectedError createDisconnectedError() {
        return new Game.PlayerDisconnectedError(player);
    }

    /**
     * On resign message callback.
     *
     * @return the message callback
     */
    @Override
    public MessageCallback onResign() {
        return message -> {
            super.onResign().onMsg(message);
            player.getGameSession().resigned(player);
        };
    }

    @Override
    public MessageCallback onQuestion() {
        return message -> {
            super.onQuestion().onMsg(message);
            if (player != null && player.getGameSession() != null) {
                player.getGameSession().askedQuestion(player, message.getQuestion());
            }
        };
    }

    /**
     * On bye message callback.
     *
     * @return the message callback
     */
    @Override
    public MessageCallback onBye() {
        return message -> {
            super.onBye().onMsg(message);

            String response = "bye";
            if (player != null) {
                response += " " + player.getUsername();

//                GameSession gameSession = player.getGameSession();
//                player.disconnect(response);
                server.playerDisconnected(player, response);

            } else
                socket.respond(Message.bye(response), message);
        };
    }

    /**
     * On username availability message callback.
     *
     * @return the message callback
     */
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

    /**
     * On db request message callback.
     *
     * @return the message callback
     */
    @Override
    public MessageCallback onDBRequest() {
        return message -> {
            super.onDBRequest().onMsg(message);
            socket.writeMessage(Message.returnDBResponse(DB.request(message.getDBRequest()), message));
        };
    }
}
