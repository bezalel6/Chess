package ver7.SharedClasses.networking;

import ver7.SharedClasses.Callbacks.MessageCallback;
import ver7.SharedClasses.messages.Message;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * AppSocket -טיפוס המייצג שקע תקשורת המאפשר העברת הודעות ברשת בין השרת ללקוח .
 * ---------------------------------------------------------------------------
 * by Ilan Peretz(ilanperets@gmail.com) 10/11/2021
 */
public class AppSocket {
    private final static int disconnectedThreshold = (int) TimeUnit.SECONDS.toMillis(10);
    private final static int keepAliveRate = (int) TimeUnit.SECONDS.toMillis(1);
    private final static boolean logErrs = false;
    private final Timer keepAlive;
    /*
    client->getStatistics
    server->player joined
    statistics<-player joined *ohno*
    */
    protected Socket msgSocket;           // Message socket
    private Long lastResponse = null;
    private ObjectOutputStream msgOS;   // Output stream to SEND Messages
    private ObjectInputStream msgIS;    // Input stream to GET Messages
    private MessagesHandler messagesHandler;
    private volatile boolean keepReading;

    public AppSocket(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    public AppSocket(Socket socket) {
        this.msgSocket = socket;

        keepAlive = createKeepAlive();

        try {
            // Create MESSAGE streams. Output Stream must be created FIRST!
            // ------------------------------------------------------------
            msgOS = new ObjectOutputStream(socket.getOutputStream());
            msgIS = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }

    private Timer createKeepAlive() {
        return new Timer(keepAliveRate, e -> {
            if (!keepReading) {
                ((Timer) e.getSource()).stop();
                return;
            }
            requestMessage(Message.isAlive(), response -> {
                if (response == null) {
                    lastResponse = Long.MIN_VALUE;
                } else {
                    lastResponse = System.currentTimeMillis();
                }
            });
            if (lastResponse != null && System.currentTimeMillis() - lastResponse >= disconnectedThreshold) {
//                messagesHandler.interruptBlocking();
                messagesHandler.onDisconnected();
            }
        });
    }

    public void requestMessage(Message requestMsg, MessageCallback onRes) {
        assert messagesHandler != null;
        messagesHandler.noBlockRequest(onRes, requestMsg);
    }

    protected AppSocket() {
        throw new Error("אין לי כח לתחזק את הdummy הזה");
//        keepAlive = null;
    }

    public void setHandlerAndStart(MessagesHandler messagesHandler) {
        this.messagesHandler = messagesHandler;
        startKeepingAlive();
        startReading();
    }

    private void startKeepingAlive() {
//        keepAlive.start();
    }

    private void startReading() {
        assert messagesHandler != null;
        keepReading = true;

        new Thread(() -> {
            while (keepReading) {
                try {
                    Message msg = (Message) msgIS.readObject();
                    messagesHandler.receivedMessage(msg);
                } catch (Exception ex) {
                    messagesHandler.onDisconnected();
//                    messagesHandler.interruptBlocking(Message.interrupt());
                    if (logErrs)
                        ex.printStackTrace();
                    break;
                }
            }
            System.out.println("done listening");
        }).start();


    }

    public MessagesHandler getMessagesHandler() {
        return messagesHandler;
    }

    public void respond(Message msg, Message respondingTo) {
        msg.setRespondingTo(respondingTo);
        writeMessage(msg);
    }

    public synchronized void writeMessage(Message msg) {

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
            keepAlive.stop();
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

    public void interruptListener() {
        messagesHandler.interruptBlocking();
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
}