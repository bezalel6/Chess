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
 * represents a communications socket able to send and receive {@link Message}s to and from another {@link AppSocket}.
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
     * Instantiates a new App socket to a specified address
     *
     * @param ip   the ip
     * @param port the port
     * @throws IOException the io exception
     */
    public AppSocket(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    /**
     * Instantiates a new App socket to a socket
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
     * Close this socket, and interrupt every method waiting for a message.
     *
     * @param err the error to interrupt with
     */
    public void close(MyError err) {
        ErrorHandler.ignore(() -> {
            interruptListener(err);
            msgSocket.close();  // will close the IS & OS streams
        });
    }

    /**
     * interrupt every method waiting for a message.
     *
     * @param err the error to interrupt with
     */
    public void interruptListener(MyError err) {
        messagesHandler.interruptBlocking(err);
    }

    /**
     * Close this socket and send a disconnected error to every method waiting for a message.
     */
    public void close() {
        close(messagesHandler.createDisconnectedError());
    }

    /**
     * send a Request and call a {@link MessageCallback} when a response message is received.
     *
     * @param requestMsg the request message
     * @param onRes      the callback to call when a response is received
     */
    public void requestMessage(Message requestMsg, MessageCallback onRes) {
        assert messagesHandler != null;
        messagesHandler.noBlockRequest(requestMsg, onRes);
    }

    /**
     * run this thread with handlers to deal with any exception that might occur.
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
     * Respond to a request message.
     *
     * @param msg          the response
     * @param respondingTo the request message
     */
    public void respond(Message msg, Message respondingTo) {
        msg.setRespondingTo(respondingTo);
        writeMessage(msg);
    }

    /**
     * Write a message.
     *
     * @param msg the message
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
     * Is this socket connected to the destination socket.
     *
     * @return <code>true</code> if connected
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
     * send a request and block this thread until a response is read. then return the response.
     *
     * @param requestMsg the request message
     * @return the response
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
     * represents an error thrown in, or related to, the App socket.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class AppSocketError extends MyError.DisconnectedError {
        /**
         * @param err the cause
         */
        public AppSocketError(Exception err) {
            super(err);
        }
    }
}
