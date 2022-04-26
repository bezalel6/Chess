package ver14.SharedClasses.Networking;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.SharedClasses.Networking.Messages.MessageType;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Threads.ThreadsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;


/**
 * Messages handler - handles all types of messages by using a hash map to make all the routing as fast as possible.
 * when a request is sent to the server, a callback is passed with it. when a response is received,
 * that callback is called with the response message.
 * <br/>
 * if a message that isn't a response is received, the handling is differed to the default callbacks map. which is where
 * most of the implementation of this abstract class comes in. the individual message type handling.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class MessagesHandler {

    /**
     * The Socket.
     */
    protected final AppSocket socket;
    /**
     * The Waiting queue. contains all blocking message callbacks. used to interrupt on disconnect or on error.
     */
    private final Vector<CompletableFuture<Message>> waiting;
    /**
     * The Default callbacks.
     */
    private final Map<MessageType, MessageCallback> defaultCallbacks;
    /**
     * The Custom callbacks. set by requesting a message. the keys are the request id.
     */
    private final Map<String, MessageCallback> customCallbacks = new HashMap<>();
    /**
     * The Chronological semaphore. some messages have chronological importance, and since all messages are processed on a new thread, some synchronization is sometimes needed
     */
    private final Semaphore chronologicalSemaphore = new Semaphore(1);
    /**
     * is expecting disconnect.
     */
    private boolean isExpectingDisconnect = false;
    /**
     * Did disconnect.
     */
    private boolean didDisconnect = false;

    {
        defaultCallbacks = new HashMap<>();
        for (MessageType messageType : MessageType.values()) {
            MessageCallback callback = switch (messageType) {
                case LOGIN -> onLogin();
                case RESIGN -> onResign();
                case ADD_TIME -> onAddTime();
                case WELCOME_MESSAGE -> onWelcomeMessage();
                case GET_GAME_SETTINGS -> onGetGameSettings();
                case WAIT_FOR_MATCH -> onWaitForMatch();
                case INIT_GAME -> onInitGame();
                case WAIT_TURN -> onWaitTurn();
                case GET_MOVE -> onGetMove();
                case THROW_ERROR -> onThrowError();
                case UPDATE_BY_MOVE -> onUpdateByMove();
                case GAME_OVER -> onGameOver();
                case ERROR -> onError();
                case QUESTION -> onQuestion();
                case BYE -> onBye();
                case USERNAME_AVAILABILITY -> onUsernameAvailability();
                case DB_REQUEST -> onDBRequest();
                case DB_RESPONSE -> onDBResponse();
                case UPDATE_SYNCED_LIST -> onUpdateSyncedList();
                case INTERRUPT -> onInterrupt();
                case CANCEL_QUESTION -> onCancelQuestion();
                default -> throw new IllegalStateException("Unexpected value: " + messageType);
            };
            defaultCallbacks.put(messageType, callback);
        }

    }

    /**
     * Instantiates a new Messages handler.
     *
     * @param socket the socket
     */
    public MessagesHandler(AppSocket socket) {
        this.socket = socket;
        waiting = new Vector<>();


    }

    /**
     * On cancel question message callback.
     *
     * @return the message callback
     */
    public MessageCallback onCancelQuestion() {
        return msg -> {
        };
    }


    /**
     * @param err
     */
    public void interruptBlocking(MyError err) {
        waiting.forEach(w -> w.complete(Message.throwError(err)));
    }

    /**
     * Blocking this thread until a response is received
     *
     * @param request the request
     * @return the message
     */
    public Message blockTilRes(Message request) {

        CompletableFuture<Message> future = new CompletableFuture<>();
        waiting.add(future);

        Message msg = null;
        noBlockRequest(request, future::complete);
        try {
            msg = future.get();
        } catch (InterruptedException e) {//if interrupted, null is fine
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        waiting.remove(future);

        if (msg == null)
            throw new MyError.DisconnectedError();

        if (msg.getMessageType() == MessageType.THROW_ERROR) {
            onThrowError().onMsg(msg);
        }

        return msg;
    }

    /**
     * No block request.
     *
     * @param request the request
     * @param onRes   the on res
     */
    public void noBlockRequest(Message request, MessageCallback onRes) {
        customCallbacks.put(request.messageID, onRes);
        socket.writeMessage(request);
    }

    /**
     * On throw error message callback.
     *
     * @return the message callback
     */
    private MessageCallback onThrowError() {
        return msg -> {
            throw (msg.getError());
        };
    }

    /**
     * Received message.
     *
     * @param message the message
     */
    public void receivedMessage(Message message) {
        if (message.getMessageType() == MessageType.BYE) {
            prepareForDisconnect();
        }
            /*
            messages need to be processed in a new thread to free blocking requests

                example:
                    client received a login message.
                    client replying with the login info and waiting for a welcome\error response message.
                    gets stuck. because the response message will never get read, bc the reading thread is waiting for a response
                 */
        ThreadsManager.createThread(() -> {
            if (message.getMessageType().chronologicalImportance) {
//                System.out.println(message.getMessageType() + " acquiring semaphore");
                chronologicalSemaphore.acquire();
//                System.out.println(message.getMessageType() + " acquired semaphore");
            }
            processMessage(message);
            if (message.getMessageType().chronologicalImportance) {
                chronologicalSemaphore.release();
//                System.out.println(message.getMessageType() + " released semaphore");
            }
        }, true).setName("Messages worker");
    }

    /**
     * Prepare for disconnect.
     */
    public void prepareForDisconnect() {
        isExpectingDisconnect = true;
    }

    /**
     * Process message.
     *
     * @param message the message
     */
    private void processMessage(Message message) {
        onAnyMsg(message);
        String respondingTo = message.getRespondingToMsgId();
        MessageCallback callback;
        synchronized (customCallbacks) {
            if (respondingTo != null && customCallbacks.containsKey(respondingTo)) {
                callback = customCallbacks.remove(respondingTo);
            } else {
                callback = defaultCallbacks.get(message.getMessageType());
            }
        }
        callback.onMsg(message);
    }

    /**
     * On any msg.
     *
     * @param message the message
     */
    public void onAnyMsg(Message message) {
//        System.out.println("received  " + message);

//        if (message.getMessageType() != MessageType.IS_ALIVE && message.getMessageType() != MessageType.ALIVE) {
//            receivedMessages.push(message);
//            if (receivedMessages.size() > 1000) {
//                throw new Error("IM GETTING WAY TOO MANY MSGS");
//            }
//        }
    }

    /**
     * On disconnected.
     */
    public final void onDisconnected() {
        if (!didDisconnect)
            onAnyDisconnection();
        else return;
        didDisconnect = true;

        if (isExpectingDisconnect) {
            onPlannedDisconnect();
        } else {
            onUnplannedDisconnect();
        }

    }

    /**
     * On any disconnection.
     */
    protected void onAnyDisconnection() {
        socket.close();
    }

    /**
     * On planned disconnect.
     */
    protected void onPlannedDisconnect() {
        synchronized (customCallbacks) {
            customCallbacks.values().forEach(messageCallback -> {
                messageCallback.onMsg(null);
            });
        }
    }

    /**
     * On unplanned disconnect.
     */
    protected void onUnplannedDisconnect() {
        synchronized (customCallbacks) {
            customCallbacks.values().forEach(messageCallback -> messageCallback.onMsg(Message.throwError(createDisconnectedError())));
        }
    }

    /**
     * Create disconnected error my error . disconnected error.
     *
     * @return the my error . disconnected error
     */
    protected MyError.DisconnectedError createDisconnectedError() {
        return new MyError.DisconnectedError();
    }

    /**
     * On login message callback.
     *
     * @return the message callback
     */
    public MessageCallback onLogin() {
        return message -> {

        };
    }

    /**
     * On resign message callback.
     *
     * @return the message callback
     */
    public MessageCallback onResign() {
        return message -> {

        };
    }

    /**
     * On add time message callback.
     *
     * @return the message callback
     */
    public MessageCallback onAddTime() {
        return message -> {

        };
    }

    /**
     * On offer draw message callback.
     *
     * @return the message callback
     */
    public MessageCallback onOfferDraw() {
        return message -> {

        };
    }

    /**
     * On welcome message message callback.
     *
     * @return the message callback
     */
    public MessageCallback onWelcomeMessage() {
        return message -> {

        };
    }

    /**
     * On get game settings message callback.
     *
     * @return the message callback
     */
    public MessageCallback onGetGameSettings() {
        return message -> {

        };
    }

    /**
     * On wait for match message callback.
     *
     * @return the message callback
     */
    public MessageCallback onWaitForMatch() {
        return message -> {

        };
    }

    /**
     * On init game message callback.
     *
     * @return the message callback
     */
    public MessageCallback onInitGame() {
        return message -> {

        };
    }

    /**
     * On wait turn message callback.
     *
     * @return the message callback
     */
    public MessageCallback onWaitTurn() {
        return message -> {

        };
    }

    /**
     * On get move message callback.
     *
     * @return the message callback
     */
    public MessageCallback onGetMove() {
        return message -> {

        };
    }

    /**
     * On update by move message callback.
     *
     * @return the message callback
     */
    public MessageCallback onUpdateByMove() {
        return message -> {

        };
    }

    /**
     * On game over message callback.
     *
     * @return the message callback
     */
    public MessageCallback onGameOver() {
        return message -> {

        };
    }

    /**
     * On error message callback.
     *
     * @return the message callback
     */
    public MessageCallback onError() {
        return message -> {

        };
    }

    /**
     * On question message callback.
     *
     * @return the message callback
     */
    public MessageCallback onQuestion() {
        return message -> {

        };
    }

    /**
     * On bye message callback.
     *
     * @return the message callback
     */
    public MessageCallback onBye() {
        return message -> {
            prepareForDisconnect();
        };
    }

    /**
     * On username availability message callback.
     *
     * @return the message callback
     */
    public MessageCallback onUsernameAvailability() {
        return message -> {

        };
    }

    /**
     * On db request message callback.
     *
     * @return the message callback
     */
    public MessageCallback onDBRequest() {
        return message -> {

        };
    }

    /**
     * On db response message callback.
     *
     * @return the message callback
     */
    public MessageCallback onDBResponse() {
        return message -> {

        };
    }

    /**
     * On update synced list message callback.
     *
     * @return the message callback
     */
    public MessageCallback onUpdateSyncedList() {
        return message -> {

        };
    }

    /**
     * On interrupt message callback.
     *
     * @return the message callback
     */
    public MessageCallback onInterrupt() {
        return message -> {
            if (!message.isResponse()) {
                socket.respond(Message.interrupt(), message);
            }
        };
    }

    /**
     * On is alive message callback.
     *
     * @return the message callback
     */
    public MessageCallback onIsAlive() {
        return message -> {
            socket.respond(Message.interrupt(), message);
        };
    }

    /**
     * On alive message callback.
     *
     * @return the message callback
     */
    public MessageCallback onAlive() {
        return message -> {

        };
    }

}
