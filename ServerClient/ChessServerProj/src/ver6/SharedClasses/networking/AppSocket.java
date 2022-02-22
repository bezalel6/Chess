package ver6.SharedClasses.networking;

import ver6.SharedClasses.Callbacks.MessageCallback;
import ver6.SharedClasses.ThrowingThread;
import ver6.SharedClasses.messages.Message;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * AppSocket -טיפוס המייצג שקע תקשורת המאפשר העברת הודעות ברשת בין השרת ללקוח .
 * ---------------------------------------------------------------------------
 * by Ilan Peretz(ilanperets@gmail.com) 10/11/2021
 */
public class AppSocket {
    private final static boolean logErrs = false;
    private final Timer keepAlive;
    /*
    client->getStatistics
    server->player joined
    statistics<-player joined *ohno*
    */
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
        keepAlive = new Timer(1000, e -> {
            if (!keepReading) {
                ((Timer) e.getSource()).stop();
                return;
            }
            writeMessage(Message.isAlive());
        });
        try {
            // Create MESSAGE streams. Output Stream must be created FIRST!
            // ------------------------------------------------------------
            msgOS = new ObjectOutputStream(socket.getOutputStream());
            msgIS = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();

        }
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

    protected AppSocket() {
        throw new Error("אין לי כח לתחזק את הdummy הזה");
//        keepAlive = null;
    }


    public void setHandlerAndStart(MessagesHandler messagesHandler) throws MyErrors {
        setHandlerAndStart(messagesHandler, ThrowingThread.ErrorHandler.create());
    }

    public void setHandlerAndStart(MessagesHandler messagesHandler, ThrowingThread.ErrorHandler errorHandler) throws MyErrors {
        this.messagesHandler = messagesHandler;
        startKeepingAlive();
        startReading(errorHandler);
    }

    private void startKeepingAlive() {
        keepAlive.start();
    }

    private void startReading(ThrowingThread.ErrorHandler errorHandler) throws MyErrors {
        assert messagesHandler != null;
        keepReading = true;

        ThrowingThread.start(_4 -> {
            while (keepReading) {
                try {
                    Message msg = (Message) msgIS.readObject();
                    messagesHandler.receivedMessage(msg);
                } catch (Exception ex) {
//                    messagesHandler.interruptBlocking(Message.interrupt());
//                    if (logErrs)
//                        ex.printStackTrace();
//                    throw ex;
//                    break;
                    messagesHandler.getCurrentHandlingErr().err(MyErrors.parse(ex));
                    break;
                }
            }
        }, errorHandler).verify();


    }

    public MessagesHandler getMessagesHandler() {
        return messagesHandler;
    }

    public void respond(Message msg, Message respondingTo) {
        msg.setRespondingTo(respondingTo);
        writeMessage(msg);
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
//        writeMessage(Message.interrupt());
        try {
            messagesHandler.interruptBlocking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * sending request and blocking til res
     *
     * @param requestMsg = "can i have x message?"
     * @return
     */
    public Message requestMessage(Message requestMsg) throws MyErrors {
        assert messagesHandler != null;
        return messagesHandler.blockTilRes(requestMsg);
    }

    public void requestMessage(Message requestMsg, MessageCallback onRes) {
        assert messagesHandler != null;
        messagesHandler.noBlockRequest(onRes, requestMsg);
    }
}
