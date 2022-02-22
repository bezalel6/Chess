package ver3;

import SharedClasses.messages.ErrorMessage;
import SharedClasses.messages.LoginInfo;
import SharedClasses.messages.LoginMessage;
import SharedClasses.messages.Message;
import ver3.view.Location;
import ver3.view.Player;
import ver3.view.View;

import javax.swing.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Client דוגמה ללקוח צאט פשוט .
 * ---------------------------------------------------------------------------
 * by Ilan Perez (ilanperets@gmail.com) 20/10/2021
 */
public class Client {
    public static final String CLIENT_WIN_TITLE = "Chat Client";
    public static final int COLS = 8;
    public static final int ROWS = 8;

    // constatns
    private static final int SERVER_DEFAULT_PORT = 1234;

    // for GUI
    private View view;

    // for Client
    private boolean clientSetupOK, clientRunOK;
    private int serverPort;
    private String serverIP;
//    private Socket socketToServer;

    private AppSocket clientSocket;
    private boolean isLoggedIn;

    /**
     * Constractor for Chat Client.
     */
    public Client() {
        view = new View(8, this, Player.WHITE, 1000);
        setupClient();
    }

    private static void log(String msg, Exception ex, JFrame... win) {
        String title = "Runtime Exception: " + msg;

        System.out.println("\n>> " + title);
        System.out.println(">> " + new String(new char[title.length()]).replace('\0', '-'));

        String errMsg = ">> " + ex.toString() + "\n";
        for (StackTraceElement element : ex.getStackTrace())
            errMsg += ">>> " + element + "\n";
        System.out.println(errMsg);

        if (win.length != 0) {
            // bring the window into front (DeIconified)
            win[0].setVisible(true);
            win[0].toFront();
            win[0].setState(JFrame.NORMAL);

            // popup dialog with the error message
            JOptionPane.showMessageDialog(win[0], msg + "\n\n" + errMsg, "Exception Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // main
    public static void main(String[] args) {
        Client client = new Client();
        client.runClient();

        System.out.println("**** Client main() finished! ****");
    }

    public void runClient() {
        if (clientSetupOK) {
            log("Connected to Server(" + clientSocket.getRemoteAddress() + ")");
            log("CLIENT(" + clientSocket.getLocalAddress() + ") Setup & Running!\n");
//            setChatMsgSendEnable(clientSocket.isLoggedIn());

            clientRunOK = true;

            // loop while client running OK
            while (clientRunOK) {

                // wait for a message from server or null if socket closed
                Message msg = clientSocket.readMessage();

                if (msg == null)
                    clientRunOK = false;
                else
                    processMsgFromServer(msg);
            }
        }
        closeClient();

        System.out.println("**** runClient() finished! ****");
    }

    private void processMsgFromServer(Message message) {
        String msg = message.getSubject();
        if (message instanceof LoginMessage) {
            isLoggedIn = false;
            msg = login();
        }
        log(msg);
    }

    private void setupClient() {
        try {
            // set the Server Adress (DEFAULT IP&PORT)
            serverPort = SERVER_DEFAULT_PORT;
            serverIP = InetAddress.getLocalHost().getHostAddress(); // IP of this computer

            // get Server Address from user
            String serverAddress = JOptionPane.showInputDialog(view.getWin(), "Enter SERVER Address [IP : PORT]", serverIP + " : " + serverPort);

            // check if Cancel button was pressed
            if (serverAddress == null)
                closeClient();

            // get server IP & PORT from input string
            serverAddress = serverAddress.replace(" ", ""); // remove all spaces
            serverIP = serverAddress.substring(0, serverAddress.indexOf(":"));
            serverPort = Integer.parseInt(serverAddress.substring(serverAddress.indexOf(":") + 1));

            // Setup connection to SERVER Address
//            socketToServer = new Socket(serverIP, serverPort);
            clientSocket = new AppSocket(new Socket(serverIP, serverPort));

            isLoggedIn = false;

            clientSetupOK = true;
        } catch (Exception exp) {
            clientSetupOK = false;
            String serverAddress = serverIP + ":" + serverPort;
            log("Client can't connect to Server(" + serverAddress + ")", exp, view.getWin());
        }
    }

    private String login() {
        return login("");
    }

    private String login(String err) {
        LoginInfo loginInfo = Login.showLoginDialog(err);

        clientSocket.writeMessage(new LoginMessage(loginInfo));
        Message response = clientSocket.readMessage();
        if (response == null) {
            return login("Error reading response from server");
        }
        if (response instanceof ErrorMessage) {
            return login(response.getSubject());
        }
        if (loginInfo.isCancel()) {
            closeClient(false);
            return null;
        }
        if (response instanceof LoginMessage) {
            loginInfo = ((LoginMessage) response).getLoginInfo();
        }
        String username = loginInfo.getUsername();
//        chatter.setId(chatterId);
//        chatter.setLoggedIn(true);
        isLoggedIn = true;
        setTitle(username);
        return response.getSubject();
    }

    private void setTitle(String username) {
        view.setTitle(CLIENT_WIN_TITLE + " (" + clientSocket.getLocalAddress() + ") " + username);
    }

    // call when window X pressed
    private void exitClient() {
        int option = JOptionPane.showConfirmDialog(view.getWin(), "Do you really want to Exit?", "Client Exit", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.OK_OPTION)
            stopClient();
    }

    private void stopClient() {
        clientSocket.close(); // will throw 'SocketException' and unblock I/O. see close() API
    }

    private void closeClient() {
        closeClient(true);
    }

    private void closeClient(boolean didLoseConnection) {
        if (clientSocket != null && clientSocket.isConnected()) {
            if (didLoseConnection) {
                String msg = "The connection with the Server is LOST!\nClient will close now...";
                JOptionPane.showMessageDialog(view.getWin(), msg, "Chat Client Error", JOptionPane.ERROR_MESSAGE);
            }
            stopClient();
        }

        log("Client Closed!");

        // close GUI
        view.getWin().dispose();

        // close client
        System.exit(0);
    }

    public void boardButtonPressed(Location to) {

    }

    private void log(String str) {
        System.out.println(str);
    }

    public void exitButtonPressed() {
        exitClient();
    }
}
