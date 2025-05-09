package ver14;

import ver14.DB.DB;
import ver14.Game.PlayerDisconnectedError;
import ver14.Players.PlayerNet.PlayerNet;
import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Networking.AppSocket;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.SharedClasses.Networking.MessagesHandler;
import ver14.SharedClasses.Threads.ErrorHandling.DisconnectedError;

import java.util.ArrayList;


/**
 * Server messages handler.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ServerMessagesHandler extends MessagesHandler {
    /**
     * The Server.
     */
    private final Server server;
    /**
     * The Player.
     */
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

    /**
     * On cancel question message callback.
     *
     * @return the message callback
     */
    @Override
    public MessageCallback onCancelQuestion() {
        return message -> {
            super.onCancelQuestion().callback(message);
            player.getPartner().cancelQuestion(message.getQuestion(), message.getCancelingQuestionCause());
        };
    }

    /**
     * On unplanned disconnect.
     */
    @Override
    protected void onUnplannedDisconnect() {
        super.onUnplannedDisconnect();
        server.playerDisconnected(player, "unexpectedly disconnected");
    }

    /**
     * Create disconnected error my error . disconnected error.
     *
     * @return the my error . disconnected error
     */
    @Override
    protected DisconnectedError createDisconnectedError() {
        return new PlayerDisconnectedError(player);
    }

    /**
     * On resign message callback.
     *
     * @return the message callback
     */
    @Override
    public MessageCallback onResign() {
        return message -> {
            super.onResign().callback(message);
            player.getGameSession().resigned(player);
        };
    }

    /**
     * On question message callback.
     *
     * @return the message callback
     */
    @Override
    public MessageCallback onQuestion() {
        return message -> {
            super.onQuestion().callback(message);
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
            super.onBye().callback(message);
            String response = "bye";
            if (player != null) {
                response += " " + player.getUsername();
                System.out.println(response);
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
            super.onUsernameAvailability().callback(message);

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
            super.onDBRequest().callback(message);

            socket.writeMessage(Message.returnDBResponse(server.dbRequest(message.getDBRequest()), message));
        };
    }
}
