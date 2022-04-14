package ver14.SharedClasses.networking;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Threads.ErrorHandling.*;
import ver14.SharedClasses.Threads.ThreadsManager;
import ver14.SharedClasses.messages.Message;
import ver14.SharedClasses.messages.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * AppSocket -טיפוס המייצג שקע תקשורת המאפשר העברת הודעות ברשת בין השרת ללקוח .
 * ---------------------------------------------------------------------------
 * by Ilan Peretz(ilanperets@gmail.com) 10/11/2021
 */
public class AppSocket extends ThreadsManager.MyThread implements ErrorContext {
    static {
//        ErrorManager.setHandler(MyError.ErrorType.AppSocketWrite, err -> {
//            AppSocket socket = (AppSocket) err.getContext(MyError.ContextType.AppSocket);
//            socket.messagesHandler.receivedMessage(null);
//        });
        ErrorManager.setHandler(ErrorType.AppSocketRead, err -> {
            AppSocket socket = (AppSocket) err.getContext(ContextType.AppSocket);
            socket.messagesHandler.onDisconnected();
            socket.close();

        });
        ErrorManager.setHandler(ErrorType.Disconnected, err -> {

        });
        ErrorManager.setHandler(ErrorType.AppSocketWrite, err -> {
//            AppSocket socket = (AppSocket) err.getContext(ContextType.AppSocket);
//            socket.messagesHandler.onDisconnected();
        });
    }

    /**
     * The Msg socket.
     */
    protected final Socket msgSocket;           // Message socket
    private final ObjectOutputStream msgOS;   // Output stream to SEND Messages
    private final ObjectInputStream msgIS;    // Input stream to GET Messages
    private MessagesHandler messagesHandler;
    private boolean keepReading;
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

        // Create MESSAGE streams. Output Stream must be created FIRST!
        // ------------------------------------------------------------
        msgOS = new ObjectOutputStream(socket.getOutputStream());
        msgOS.flush();
        msgIS = new ObjectInputStream(socket.getInputStream());
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
        keepReading = true;
        while (keepReading) {
            try {
                Message msg = (Message) msgIS.readObject();
                messagesHandler.receivedMessage(msg);
            } catch (Exception e) {
                didDisconnect = true;
                keepReading = false;
                throw MyError.AppSocket(true, this, e);
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
        if (msg.getMessageType() == MessageType.BYE)
            messagesHandler.setBye();
        try {
            msgOS.writeObject(msg);
            msgOS.flush(); // send object now! (dont wait)
        } catch (Exception e) {
            didDisconnect = true;
            throw MyError.AppSocket(false, this, e);
//            ErrorHandler.thrown(ex);
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
     * @return message
     */
    public Message requestMessage(Message requestMsg) {
        assert messagesHandler != null;
        return messagesHandler.blockTilRes(requestMsg);
    }

    /**
     * Stop reading.
     */
    public void stopReading() {
        keepReading = false;
        interruptListener(null);
    }

    /**
     * Interrupt listener.
     */
    public void interruptListener(MyError err) {
        messagesHandler.interruptBlocking(err);
        close();

//        interrupt();
    }

    /**
     * Close.
     */
    public void close() {
        ErrorHandler.ignore(() -> {
            keepReading = false;
            if (messagesHandler != null) {
                messagesHandler.interruptBlocking(new MyError(ErrorType.Disconnected));
            }
            msgSocket.close();  // will close the IS & OS streams
        });
    }

    /**
     * Context type my error . context type.
     *
     * @return the my error . context type
     */
    @Override
    public ContextType contextType() {
        return ContextType.AppSocket;
    }
}
