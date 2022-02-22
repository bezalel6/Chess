package ver5.SharedClasses.networking;

import ver5.SharedClasses.messages.Message;
import ver5.SharedClasses.messages.MessageCallback;
import ver5.SharedClasses.messages.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * AppSocket -טיפוס המייצג שקע תקשורת המאפשר העברת הודעות ברשת בין השרת ללקוח .
 * ---------------------------------------------------------------------------
 * by Ilan Peretz(ilanperets@gmail.com) 10/11/2021
 */
public class AppSocket {
    /*
    client->getStatistics
    server->player joined
    statistics<-player joined *ohno*
    */
    private final HashMap<Integer, MessageCallback> callbackHashMap = new HashMap<>();
    protected Socket msgSocket;           // Message socket
    private KeepAliveSocket aliveSocket = null;
    private AppSocket backgroundSocket = null;
    private MessagesHandler messagesHandler;
    private ObjectOutputStream msgOS;   // Output stream to SEND Messages
    private ObjectInputStream msgIS;    // Input stream to GET Messages

    public AppSocket(Socket socket, Socket alive, Socket background) {
        this(socket);
        this.aliveSocket = new KeepAliveSocket(alive);
        this.backgroundSocket = new AppSocket(background);
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

    protected AppSocket() {

    }

    public void setMessagesHandler(MessagesHandler messagesHandler) {
        this.messagesHandler = messagesHandler;
    }

    public KeepAliveSocket getAliveSocket() {
        return aliveSocket;
    }

    public void sendIsAlive() {
        writeMessage(Message.isAlive());
    }

    public void writeMessage(Message msg) {
        try {
            msgOS.writeObject(msg);
            msgOS.flush(); // send object now! (dont wait)
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startReading() {
        new Thread(() -> {
            while (true) {

            }
        }).start();
    }

    public void close() {
        try {
            msgSocket.close();  // will close the IS & OS streams
            if (aliveSocket != null)
                aliveSocket.stop();
            if (backgroundSocket != null) {
                backgroundSocket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
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

    public void interruptListener() {
        writeMessage(Message.interrupt());
    }

    public AppSocket getBackgroundSocket() {
        return backgroundSocket;
    }

    //not blocking
    public void readMessage(MessageCallback onMsg) {
        new Thread(() -> {
            onMsg.callback(readMessage());
        }).start();
    }

    public Message readMessage() {
        Message msg = null;
        try {
            msg = (Message) msgIS.readObject();
            if (msg.getMessageType() == MessageType.INTERRUPT) {
                writeMessage(Message.stopRead());
            }
        } catch (Exception ex) {
//            ex.printStackTrace();
        }
        return msg;
    }
}
