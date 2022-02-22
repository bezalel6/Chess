package ver4.SharedClasses.networking;

import ver4.SharedClasses.messages.Message;
import ver4.SharedClasses.messages.MessageType;

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

    private Socket msgSocket;           // Message socket
    private ObjectOutputStream msgOS;   // Output stream to SEND Messages 
    private ObjectInputStream msgIS;    // Input stream to GET Messages

    public AppSocket(Socket socket) {
        this.msgSocket = socket;
        try {
            // Create MESSAGE streams. Output Stream must be create FIRST!
            // ------------------------------------------------------------
            msgOS = new ObjectOutputStream(socket.getOutputStream());
            msgIS = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ObjectInputStream getMsgIS() {
        return msgIS;
    }

    public void writeMessage(Message msg) {
        try {
            msgOS.writeObject(msg);
            msgOS.flush(); // send object now! (dont wait)
        } catch (Exception ex) {
//            ex.printStackTrace();
        }
    }

    public Message readMessage() {
        Message msg = null;
        try {
            msg = (Message) msgIS.readObject();
            if (msg.getMessageType() == MessageType.IS_ALIVE) {
                Message alive = Message.alive();
                writeMessage(alive);
                return alive;
            }


        } catch (Exception ex) {
//            ex.printStackTrace();
        }
        return msg;
    }

    public void sendIsAlive() {
        writeMessage(Message.isAlive());
    }

    public Socket getMsgSocket() {
        return msgSocket;
    }

    public void close() {
        try {
            msgSocket.close();  // will close the IS & OS streams 
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
}
