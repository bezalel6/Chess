package ver6.server;

import ver6.SharedClasses.networking.AppSocket;

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
            socketToClient = new AppSocket(mainSocket);
        } catch (IOException exp) {
            exp.printStackTrace();
        }
        return socketToClient;
    }
}
