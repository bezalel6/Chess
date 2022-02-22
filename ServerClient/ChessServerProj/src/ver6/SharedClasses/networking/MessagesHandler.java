package ver6.SharedClasses.networking;

import ver6.SharedClasses.Callbacks.MessageCallback;
import ver6.SharedClasses.ThrowingThread;
import ver6.SharedClasses.messages.Message;
import ver6.SharedClasses.messages.MessageType;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class MessagesHandler {

    public final AppSocket socket;
    private final Stack<Message> receivedMessages = new Stack<>();
    private final Map<String, MessageCallback> onReceiveCallback = new HashMap<>();
    private String currentBlockingMsgId = null;
    private ThrowingThread.ErrorHandler currentHandlingErr = null;

    public MessagesHandler(AppSocket socket) {
        this.socket = socket;
    }
//    todo save time on switching message type
//    final Map<MessageType, MessageCallback> defaultCallbacks = new HashMap<>();

    public ThrowingThread.ErrorHandler getCurrentHandlingErr() {
        return currentHandlingErr;
    }

    public synchronized Message blockTilRes(Message request) throws MyErrors {
        currentHandlingErr = ThrowingThread.ErrorHandler.create();

        AtomicReference<Message> ret = new AtomicReference<>(null);
        AtomicBoolean keepBlocking = new AtomicBoolean(true);
        currentBlockingMsgId = request.messageID;
        noBlockRequest(msg -> {
            ret.set(msg);
            keepBlocking.set(false);
        }, request);
        System.out.println("started blocking " + request);
        do {
            currentHandlingErr.verify();
        } while (keepBlocking.get());
        System.out.println("stopped blocking " + request);
        currentBlockingMsgId = null;
        return ret.get();
    }

    public void noBlockRequest(MessageCallback callback, Message request) {
        onReceiveCallback.put(request.messageID, callback);
        socket.writeMessage(request);
    }

    public void interruptBlocking() {
        interruptBlocking(Message.interrupt());
    }

    public void interruptBlocking(Message interruptWith) {
        if (currentBlockingMsgId != null && onReceiveCallback.containsKey(currentBlockingMsgId))
            onReceiveCallback.remove(currentBlockingMsgId).onRes(interruptWith);
    }

    public void receivedMessage(Message message) throws Exception {
            /*
            has to be in a new thread to process messages that free blocking requests

                example:
                    client received a login message.
                    sending the login info and waiting for a welcome\error response message.
                    gets stuck. because the response message will never get read.
                 */
        if (message != null && !message.getMessageType().shouldBlock()) {
            ThrowingThread.start(i -> {
                processMessage(message);
            }).verify();
        } else {
            processMessage(message);
        }
    }

    private void processMessage(Message message) throws Exception {
        if (message == null) {
//            onDisconnected();
//            throw new Errors.Disconnected();
            currentHandlingErr.err(new MyErrors.Disconnected());
//            return;
        }
        boolean log = message.getMessageType() != MessageType.IS_ALIVE && message.getMessageType() != MessageType.ALIVE;
        if (log)
            System.out.println("received  " + message);

        onAnyMsg(message);
        String respondingTo = message.getRespondingToMsgId();
        if (respondingTo != null && onReceiveCallback.containsKey(respondingTo)) {
            onReceiveCallback.remove(respondingTo).onRes(message);
            return;
        }
        switch (message.getMessageType()) {
            case LOGIN -> {
                onLogin(message);
            }
            case RESIGN -> {
                onResign(message);
            }
            case ADD_TIME -> {
                onAddTime(message);
            }
            case OFFER_DRAW -> {
                onOfferDraw(message);
            }
            case WELCOME_MESSAGE -> {
                onWelcomeMessage(message);
            }
            case GET_GAME_SETTINGS -> {
                onGetGameSettings(message);
            }
            case WAIT_FOR_MATCH -> {
                onWaitForMatch(message);
            }
            case INIT_GAME -> {
                onInitGame(message);
            }
            case WAIT_TURN -> {
                onWaitTurn(message);
            }
            case GET_MOVE -> {
                onGetMove(message);
            }
            case UPDATE_BY_MOVE -> {
                onUpdateByMove(message);
            }
            case GAME_OVER -> {
                onGameOver(message);
            }
            case ERROR -> {
                onError(message);
            }
            case QUESTION -> {
                onQuestion(message);
            }
            case BYE -> {
                onBye(message);
            }
            case USERNAME_AVAILABILITY -> {
                onUsernameAvailability(message);
            }
            case PLAYERS_STATISTICS -> {
                onPlayersStatistics(message);
            }
            case UPDATE_SYNCED_LIST -> {
                onUpdateSyncedList(message);
            }
            case INTERRUPT -> {
                onInterrupt(message);
            }
            case IS_ALIVE -> {
                onIsAlive(message);
            }
            case ALIVE -> {
                onAlive(message);
            }
            default -> {
                throw new Error("message type not implemented " + message.getMessageType());
            }
        }
        if (log)
            System.out.println("finished handling " + message);
    }

    public void onAnyMsg(Message message) {
        if (message.getMessageType() != MessageType.IS_ALIVE && message.getMessageType() != MessageType.ALIVE) {
            receivedMessages.push(message);
            if (receivedMessages.size() > 1000) {
                throw new Error("IM GETTING WAY TOO MANY MSGS");
            }
        }
    }

    //region auto gen
    public void onLogin(Message message) throws MyErrors {

    }

    public void onResign(Message message) {

    }

    public void onAddTime(Message message) {

    }

    public void onOfferDraw(Message message) {

    }

    public void onWelcomeMessage(Message message) {

    }

    public void onGetGameSettings(Message message) {

    }

    public void onWaitForMatch(Message message) {

    }

    public void onInitGame(Message message) {

    }

    public void onWaitTurn(Message message) {

    }

    public void onGetMove(Message message) {

    }

    public void onUpdateByMove(Message message) {

    }

    public void onGameOver(Message message) {

    }

    public void onError(Message message) {

    }

    public void onQuestion(Message message) {

    }

    public void onBye(Message message) {

    }

    public void onUsernameAvailability(Message message) throws MyErrors {

    }

    public void onPlayersStatistics(Message message) throws MyErrors {

    }

    public void onUpdateSyncedList(Message message) {

    }

    public void onInterrupt(Message message) {
        if (!message.isResponse()) {
            socket.writeMessage(new Message(MessageType.INTERRUPT, message));
        }
    }

    //endregion

    public void onIsAlive(Message message) {
        socket.writeMessage(Message.alive());
    }

    public void onAlive(Message message) {

    }

    public void onDisconnected() {
        socket.close();
    }

}
