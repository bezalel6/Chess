package ver13.SharedClasses.networking;

import ver13.SharedClasses.Callbacks.MessageCallback;
import ver13.SharedClasses.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * AppSocket -טיפוס המייצג שקע תקשורת המאפשר העברת הודעות ברשת בין השרת ללקוח .
 * ---------------------------------------------------------------------------
 * by Ilan Peretz(ilanperets@gmail.com) 10/11/2021
 */
public class AppSocket {
    private final static boolean logErrs = true;
    protected Socket msgSocket;           // Message socket
    private ObjectOutputStream msgOS;   // Output stream to SEND Messages
    private ObjectInputStream msgIS;    // Input stream to GET Messages
    private MessagesHandler messagesHandler;
    private volatile boolean keepReading;

    public AppSocket(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    public AppSocket(Socket socket) {
        this.msgSocket = socket;
        try {
            // Create MESSAGE streams. Output Stream must be created FIRST!
            // ------------------------------------------------------------
            msgOS = new ObjectOutputStream(socket.getOutputStream());
            msgIS = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }


    public void requestMessage(Message requestMsg, MessageCallback onRes) {
        assert messagesHandler != null;
        messagesHandler.noBlockRequest(requestMsg, onRes);
    }

    public void startReading() {
        assert messagesHandler != null;
        keepReading = true;

        new Thread(() -> {
            while (keepReading) {
                Message msg;
                try {
                    msg = (Message) msgIS.readObject();
                } catch (Exception ex) {
                    if (ex instanceof SocketException || logErrs)
                        ex.printStackTrace();
                    msg = null;
                }
                messagesHandler.receivedMessage(msg);
            }
        }).start();


    }

    public MessagesHandler getMessagesHandler() {
        return messagesHandler;
    }

    public void setMessagesHandler(MessagesHandler messagesHandler) {
        this.messagesHandler = messagesHandler;
    }

    public void respond(Message msg, Message respondingTo) {
        msg.setRespondingTo(respondingTo);
        writeMessage(msg);
    }

    public void writeMessage(Message msg) {
        try {
            msgOS.writeObject(msg);
            msgOS.flush(); // send object now! (dont wait)
        } catch (Exception ex) {
            if (logErrs)
                ex.printStackTrace();
        }
    }

    public void close() {
        try {
            if (messagesHandler != null) {
                messagesHandler.interruptBlocking();
            }
            keepReading = false;
            msgSocket.close();  // will close the IS & OS streams
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLocalAddress() {
        return msgSocket.getLocalSocketAddress().toString().substring(1);
    }

    public String getRemoteAddress() {
        return msgSocket.getRemoteSocketAddress().toString().substring(1);
    }

    public boolean isConnected() {
        return msgSocket != null && !msgSocket.isClosed();
    }

    public boolean isClosed() {
        return msgSocket != null && msgSocket.isClosed();
    }

    /**
     * sending request and blocking til res
     *
     * @param requestMsg = "can i have x message?"
     * @return
     */
    public Message requestMessage(Message requestMsg) {
        assert messagesHandler != null;
        return messagesHandler.blockTilRes(requestMsg);
    }

    public void stopReading() {
        keepReading = false;
        interruptListener();
    }

    public void interruptListener() {
        messagesHandler.interruptBlocking();
    }
}
