package ver14.SharedClasses.networking;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorContext;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorHandler;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorManager;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Threads.ThreadsManager;
import ver14.SharedClasses.messages.Message;

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
    private static final ErrorHandler readErr = err -> {
        AppSocket socket = (AppSocket) err.getContext(MyError.ContextType.AppSocket);
        socket.messagesHandler.receivedMessage(null);
    };
    private static ErrorHandler writeErr = err -> {
        AppSocket socket = (AppSocket) err.getContext(MyError.ContextType.AppSocket);
        socket.messagesHandler.receivedMessage(null);
    };

    static {
        ErrorManager.setHandler(MyError.ErrorType.AppSocketWrite, AppSocket.writeErr);
        ErrorManager.setHandler(MyError.ErrorType.AppSocketRead, AppSocket.readErr);
    }

    /**
     * The Msg socket.
     */
    protected final Socket msgSocket;           // Message socket
    private final ObjectOutputStream msgOS;   // Output stream to SEND Messages
    private final ObjectInputStream msgIS;    // Input stream to GET Messages
    private MessagesHandler messagesHandler;
    private volatile boolean keepReading;

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
            } catch (Throwable e) {
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
    public void writeMessage(Message msg) {
        try {
            msgOS.writeObject(msg);
            msgOS.flush(); // send object now! (dont wait)
        } catch (Throwable e) {
            throw MyError.AppSocket(false, this, e);
//            ErrorHandler.thrown(ex);
        }
    }

    /**
     * Close.
     */
    public void close() {
        try {
            if (messagesHandler != null) {
                messagesHandler.interruptBlocking();
            }
            keepReading = false;
            msgSocket.close();  // will close the IS & OS streams
        } catch (Exception e) {
//            ErrorHandler.thrown(e);
        }
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
     * Is connected boolean.
     *
     * @return the boolean
     */
    public boolean isConnected() {
        return msgSocket != null && !msgSocket.isClosed();
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
        interruptListener();
    }

    /**
     * Interrupt listener.
     */
    public void interruptListener() {
        messagesHandler.interruptBlocking();
    }

    /**
     * Context type my error . context type.
     *
     * @return the my error . context type
     */
    @Override
    public MyError.ContextType contextType() {
        return MyError.ContextType.AppSocket;
    }
}
