package ver8.SharedClasses.networking;

import ver8.SharedClasses.Callbacks.MessageCallback;
import ver8.SharedClasses.messages.Message;
import ver8.SharedClasses.messages.MessageType;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class MessagesHandler {
    protected final AppSocket socket;
    private final Map<MessageType, MessageCallback> defaultCallbacks;
    private final Stack<Message> receivedMessages = new Stack<>();
    private final Map<String, MessageCallback> customCallbacks = new HashMap<>();
    private String currentBlockingMsgId = null;

    {
        defaultCallbacks = new HashMap<>();

        for (MessageType messageType : MessageType.values()) {
            MessageCallback callback = switch (messageType) {
                case LOGIN -> onLogin();
                case RESIGN -> onResign();
                case ADD_TIME -> onAddTime();
                case OFFER_DRAW -> onOfferDraw();
                case WELCOME_MESSAGE -> onWelcomeMessage();
                case GET_GAME_SETTINGS -> onGetGameSettings();
                case WAIT_FOR_MATCH -> onWaitForMatch();
                case INIT_GAME -> onInitGame();
                case WAIT_TURN -> onWaitTurn();
                case GET_MOVE -> onGetMove();
                case UPDATE_BY_MOVE -> onUpdateByMove();
                case GAME_OVER -> onGameOver();
                case ERROR -> onError();
                case QUESTION -> onQuestion();
                case BYE -> onBye();
                case USERNAME_AVAILABILITY -> onUsernameAvailability();
                case PLAYERS_STATISTICS -> onPlayersStatistics();
                case UPDATE_SYNCED_LIST -> onUpdateSyncedList();
                case INTERRUPT -> onInterrupt();
                case IS_ALIVE -> onIsAlive();
                case ALIVE -> onAlive();
            };
            defaultCallbacks.put(messageType, callback);
        }

    }

    public MessagesHandler(AppSocket socket) {
        this.socket = socket;
    }

    public Message blockTilRes(Message request) {

        AtomicReference<Message> ret = new AtomicReference<>(null);
        AtomicBoolean keepBlocking = new AtomicBoolean(true);
        currentBlockingMsgId = request.messageID;
        noBlockRequest(msg -> {
            ret.set(msg);
            keepBlocking.set(false);
        }, request);
        System.out.println("started blocking " + request);

        while (keepBlocking.get()) Thread.onSpinWait();

        System.out.println("stopped blocking " + request);
        currentBlockingMsgId = null;
        return ret.get();
    }

    public void noBlockRequest(MessageCallback callback, Message request) {
        customCallbacks.put(request.messageID, callback);
        socket.writeMessage(request);
    }

    public void receivedMessage(Message message) {
            /*
            some messages need to be processed in a new thread to free blocking requests

                example:
                    client received a login message.
                    sending the login info and waiting for a welcome\error response message.
                    gets stuck. because the response message will never get read.
                 */
        if (message != null && !message.getMessageType().shouldBlock()) {
            new Thread(() -> {
                processMessage(message);
            }).start();
        } else {
            processMessage(message);
        }
    }

    private void processMessage(Message message) {
        if (message == null) {
            onDisconnected();
            return;
        }
        boolean log = message.getMessageType() != MessageType.IS_ALIVE && message.getMessageType() != MessageType.ALIVE;
//        if (log)
        System.out.println("received  " + message);

        onAnyMsg(message);
        String respondingTo = message.getRespondingToMsgId();
        if (respondingTo != null && customCallbacks.containsKey(respondingTo)) {
            customCallbacks.remove(respondingTo).onMsg(message);
            return;
        }
        defaultCallbacks.get(message.getMessageType()).onMsg(message);
        if (log)
            System.out.println("finished handling " + message);
    }

    public void onDisconnected() {
        interruptBlocking();
        socket.close();
    }


    public void onAnyMsg(Message message) {
//        if (message.getMessageType() != MessageType.IS_ALIVE && message.getMessageType() != MessageType.ALIVE) {
//            receivedMessages.push(message);
//            if (receivedMessages.size() > 1000) {
//                throw new Error("IM GETTING WAY TOO MANY MSGS");
//            }
//        }
    }


    public void interruptBlocking() {
        interruptBlocking(Message.interrupt());
    }

    public void interruptBlocking(Message interruptWith) {
        if (currentBlockingMsgId != null && customCallbacks.containsKey(currentBlockingMsgId)) {
            customCallbacks.remove(currentBlockingMsgId).onMsg(interruptWith);
        }
    }

    public MessageCallback onLogin() {
        return message -> {

        };
    }

    public MessageCallback onResign() {
        return message -> {

        };
    }

    public MessageCallback onAddTime() {
        return message -> {

        };
    }

    public MessageCallback onOfferDraw() {
        return message -> {

        };
    }

    public MessageCallback onWelcomeMessage() {
        return message -> {

        };
    }

    public MessageCallback onGetGameSettings() {
        return message -> {

        };
    }

    public MessageCallback onWaitForMatch() {
        return message -> {

        };
    }

    public MessageCallback onInitGame() {
        return message -> {

        };
    }

    public MessageCallback onWaitTurn() {
        return message -> {

        };
    }

    public MessageCallback onGetMove() {
        return message -> {

        };
    }

    public MessageCallback onUpdateByMove() {
        return message -> {

        };
    }

    public MessageCallback onGameOver() {
        return message -> {

        };
    }

    public MessageCallback onError() {
        return message -> {

        };
    }

    public MessageCallback onQuestion() {
        return message -> {

        };
    }

    public MessageCallback onBye() {
        return message -> {

        };
    }

    public MessageCallback onUsernameAvailability() {
        return message -> {

        };
    }

    public MessageCallback onPlayersStatistics() {
        return message -> {

        };
    }

    public MessageCallback onUpdateSyncedList() {
        return message -> {

        };
    }

    public MessageCallback onInterrupt() {
        return message -> {
            if (!message.isResponse()) {
                socket.respond(Message.interrupt(), message);
            }
        };
    }

    public MessageCallback onIsAlive() {
        return message -> {
            socket.respond(Message.interrupt(), message);
        };
    }

    public MessageCallback onAlive() {
        return message -> {

        };
    }

}
