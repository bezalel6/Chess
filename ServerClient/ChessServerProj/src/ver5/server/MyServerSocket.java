package ver5.server;

import ver5.SharedClasses.networking.AppSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServerSocket extends ServerSocket {

    public MyServerSocket(int port) throws IOException {
        super(port);
    }

    public AppSocket acceptAppSocket() {
        AppSocket socketToClient = null;

        System.out.println("Waiting for client ...");
        //todo make sure all sockets accept the same client
        try {
            // Wait for a new client to connect. return client socket.
            Socket mainSocket = accept(); // blocking method
            Socket alive = accept();// blocking method
            Socket background = accept();// blocking method
//            Socket alive = aliveServerSocket.accept();// blocking method
//            Socket background = backgroundServerSocket.accept();// blocking method
            socketToClient = new AppSocket(mainSocket, alive, background);
        } catch (IOException exp) {
            exp.printStackTrace();
        }
        return socketToClient;
    }
}
