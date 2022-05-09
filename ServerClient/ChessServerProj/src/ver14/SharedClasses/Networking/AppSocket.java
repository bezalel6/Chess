package ver14.SharedClasses.Networking;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.SharedClasses.Networking.Messages.MessageType;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorHandler;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Threads.MyThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * App socket - represents a communications socket able to send and receive  messages from the client to the server and vice versa.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class AppSocket extends MyThread {

    /**
     * The Msg socket.
     */
    protected final Socket msgSocket;
    /**
     * The Msg Output stream to SEND Messages.
     */
    private final ObjectOutputStream msgOS;
    /**
     * The Msg Input stream to GET Messages.
     */
    private final ObjectInputStream msgIS;
    private MessagesHandler messagesHandler;
    private boolean didDisconnect = false;

    /**
     * Instantiates a new App socket.
     *
     * @param ip   the ip
     * @param port the port
     * @throws IOException the io exception
     */
    public AppSocket(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    /**
     * Instantiates a new App socket.
     *
     * @param socket the socket
     * @throws IOException the io exception
     */
    public AppSocket(Socket socket) throws IOException {
        this.msgSocket = socket;

        addHandler(AppSocketError.class, e -> {
            didDisconnect = true;
            if (messagesHandler != null) {
                messagesHandler.onDisconnected();
            }
            close(e);
        });

        // Create MESSAGE streams. Output Stream must be created FIRST!
        // ------------------------------------------------------------
        msgOS = new ObjectOutputStream(socket.getOutputStream());
        msgOS.flush();
        msgIS = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Close.
     *
     * @param err the error
     */
    public void close(MyError err) {
        ErrorHandler.ignore(() -> {
            interruptListener(err);
            msgSocket.close();  // will close the IS & OS streams
        });
    }

    /**
     * Interrupt listener.
     *
     * @param err the err to interrupt with
     */
    public void interruptListener(MyError err) {
        messagesHandler.interruptBlocking(err);
    }

    /**
     * Close.
     */
    public void close() {
        close(messagesHandler.createDisconnectedError());
    }

    /**
     * Request message.
     *
     * @param requestMsg the request msg
     * @param onRes      the on res
     */
    public void requestMessage(Message requestMsg, MessageCallback onRes) {
        assert messagesHandler != null;
        messagesHandler.noBlockRequest(requestMsg, onRes);
    }

    /**
     * Handled run.
     */
    @Override
    protected void handledRun() {
        assert messagesHandler != null;
        while (!didDisconnect) {
            try {
                Message msg = (Message) msgIS.readObject();
                messagesHandler.receivedMessage(msg);
            } catch (Exception e) {
                throw new AppSocketError(e);
            }
        }
    }

    /**
     * Gets messages handler.
     *
     * @return the messages handler
     */
    public MessagesHandler getMessagesHandler() {
        return messagesHandler;
    }

    /**
     * Sets messages handler.
     *
     * @param messagesHandler the messages handler
     */
    public void setMessagesHandler(MessagesHandler messagesHandler) {
        this.messagesHandler = messagesHandler;
    }

    /**
     * Respond.
     *
     * @param msg          the msg
     * @param respondingTo the responding to
     */
    public void respond(Message msg, Message respondingTo) {
        msg.setRespondingTo(respondingTo);
        writeMessage(msg);
    }

    /**
     * Write message.
     *
     * @param msg the msg
     */
    public synchronized void writeMessage(Message msg) {
        if (!isConnected())
            return;
        if (messagesHandler != null && msg.getMessageType() == MessageType.BYE)
            messagesHandler.prepareForDisconnect();
        try {
            msgOS.writeObject(msg);
            msgOS.flush(); // send object now! (dont wait)
        } catch (Exception e) {
            throw new AppSocketError(e);
        }
    }

    /**
     * Is connected boolean.
     *
     * @return the boolean
     */
    public boolean isConnected() {
        return !didDisconnect && msgSocket != null && !msgSocket.isClosed() && msgSocket.isConnected();
    }

    /**
     * Gets local address.
     *
     * @return the local address
     */
    public String getLocalAddress() {
        return msgSocket.getLocalSocketAddress().toString().substring(1);
    }

    /**
     * Gets remote address.
     *
     * @return the remote address
     */
    public String getRemoteAddress() {
        return msgSocket.getRemoteSocketAddress().toString().substring(1);
    }

    /**
     * Is closed boolean.
     *
     * @return the boolean
     */
    public boolean isClosed() {
        return msgSocket != null && msgSocket.isClosed();
    }

    /**
     * sending request and blocking til res
     *
     * @param requestMsg = "can i have x message?"
     * @return response
     */
    public Message requestMessage(Message requestMsg) {
        assert messagesHandler != null;
        return messagesHandler.blockTilRes(requestMsg);
    }

    /**
     * Stop reading.
     */
    public void stopReading() {
        System.out.println("stopping reading");
        didDisconnect = true;
        interruptListener(null);
    }

    /**
     * App socket error .
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class AppSocketError extends MyError.DisconnectedError {
        /**
         * @param err err
         */
        public AppSocketError(Exception err) {
            super(err);
        }
    }
}
