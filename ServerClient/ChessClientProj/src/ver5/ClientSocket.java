package ver5;

import ver5.SharedClasses.networking.AppSocket;

import java.io.IOException;
import java.net.Socket;

public class ClientSocket extends AppSocket {
    private final int serverPort;
    private final String serverIP;

    public ClientSocket(int serverPort, String serverIP) throws IOException {
        super(new Socket(serverIP, serverPort), new Socket(serverIP, serverPort), new Socket(serverIP, serverPort));
//        super(new Socket(serverIP, serverPort + PortsInfo.normal), new Socket(serverIP, serverPort + PortsInfo.alive), new Socket(serverIP, serverPort + PortsInfo.background));
        this.serverPort = serverPort;
        this.serverIP = serverIP;
    }
}
