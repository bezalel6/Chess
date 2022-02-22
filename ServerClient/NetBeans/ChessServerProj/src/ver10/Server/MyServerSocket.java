package ver10.Server;

import ver10.SharedClasses.networking.AppSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServerSocket extends ServerSocket {

    private Thread messagesThread = null;

    private AppSocket serverMessagesListener;

    public MyServerSocket(int port) throws IOException {
        super(port);
        this.messagesThread = new Thread(() -> {
            assert messagesThread != null;
            while (!messagesThread.isInterrupted()) {
                try {

//                    Message msg = (Message)
                } catch (Exception e) {

                }
            }
        });
    }

//    private HashMap<String , MessagesHandler>;

    public AppSocket acceptAppSocket() {
        AppSocket socketToClient = null;

        System.out.println("Waiting for client ...");
        try {
            // Wait for a new client to connect. return client socket.
            Socket mainSocket = accept(); // blocking method
            socketToClient = new AppSocket(mainSocket);
        } catch (IOException exp) {
            exp.printStackTrace();
        }
        return socketToClient;
    }

    public void startReading() {
        messagesThread.start();
    }

    public void stopReading() {
        messagesThread.interrupt();
    }

    public void addClientSocket(AppSocket socket) {

    }
}
