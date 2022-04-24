package ver14;

import ver14.SharedClasses.Networking.AppSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * My server socket - represents a server sockets accepting AppSockets.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MyServerSocket extends ServerSocket {


    /**
     * Instantiates a new My server socket.
     *
     * @param port the server port
     * @throws IOException the io exception
     */
    public MyServerSocket(int port) throws IOException {
        super(port);
    }


    /**
     * Accept app socket.
     *
     * @return the accepted app socket
     */
    public AppSocket acceptAppSocket() {
        AppSocket socketToClient = null;

        System.out.println("Waiting for client ...");
        try {
            // Wait for a new client to connect. return client socket.
            Socket socket = accept(); // blocking method
            socketToClient = new AppSocket(socket);
        } catch (Throwable exp) {
//            exp.printStackTrace();
        }
        return socketToClient;
    }

    /**
     * Close.
     *
     * @throws IOException the io exception
     */
    @Override
    public void close() throws IOException {
        System.out.println("closing socket!!!");
        super.close();
    }
}
