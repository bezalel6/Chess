package ver5.SharedClasses.networking;

import ver5.SharedClasses.Callback;
import ver5.SharedClasses.messages.Message;

import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

public class KeepAliveSocket extends AppSocket {
    private static final int msgsRateInMillis = 500;
    private static final int maxRetries = 5;
    private int numOfRetries;
    private ArrayList<Callback> onDisconnectList;
    private volatile boolean keepRunning;

    public KeepAliveSocket(Socket socket) {
        super(socket);
        numOfRetries = 0;
        onDisconnectList = new ArrayList<>();
        try {
            msgSocket.setSoTimeout(msgsRateInMillis);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void addOnDisconnect(Callback... onDisconnect) {
        onDisconnectList.addAll(Arrays.asList(onDisconnect));
    }

    public void start(Callback... onDisconnect) {
        addOnDisconnect(onDisconnect);
        keepRunning = true;
        new Thread(() -> {
            while (keepRunning) {
                sendIsAlive();
                Message msg = readMessage();
                if (msg == null) {
                    onDisconnectList.forEach(Callback::callback);
                    break;
                }
                writeMessage(Message.alive());
            }
        }).start();
    }

    public void stop() {
        keepRunning = false;
        close();
    }

    @Override
    public Message readMessage() {
        Message message = super.readMessage();
        if (message == null && numOfRetries < maxRetries) {
            numOfRetries++;
            message = readMessage();
        }
        if (message != null)
            numOfRetries = 0;
        return message;
    }
}
