package ver14;

import ver14.SharedClasses.networking.AppSocket;

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
        try {
            // Wait for a new client to connect. return client socket.
            Socket socket = accept(); // blocking method
            socketToClient = new AppSocket(socket);
        } catch (Throwable exp) {
            exp.printStackTrace();
        }
        return socketToClient;
    }

}
