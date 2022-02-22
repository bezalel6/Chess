package ver2login;

import SharedClasses.messages.ErrorMessage;
import SharedClasses.messages.LoginInfo;
import SharedClasses.messages.LoginMessage;
import SharedClasses.messages.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Client דוגמה ללקוח צאט פשוט .
 * ---------------------------------------------------------------------------
 * by Ilan Perez (ilanperets@gmail.com) 20/10/2021
 */
public class Client {
    public static final String CLIENT_WIN_TITLE = "Chat Client";
    // constatns
    private static final int SERVER_DEFAULT_PORT = 1234;
    private static final Dimension CLIENT_WIN_SIZE = new Dimension(700, 400);
    private static final Color CLIENT_LOG_BGCOLOR = Color.LIGHT_GRAY;
    private static final Color CLIENT_LOG_FGCOLOR = Color.BLACK;
    private static final Font CLIENT_LOG_FONT = new Font("Consolas", Font.PLAIN, 20); // Font.MONOSPACED

    // for GUI
    private JTextArea areaLog;
    private JFrame frmWin;
    private JTextField filedMsg;
    private JButton btnSend;

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
        createClientGUI();
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

                setChatMsgSendEnable(isLoggedIn);
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
            String serverAddress = JOptionPane.showInputDialog(frmWin, "Enter SERVER Address [IP : PORT]", serverIP + " : " + serverPort);

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
            log("Client can't connect to Server(" + serverAddress + ")", exp, frmWin);
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
            closeClient(null, null);
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
        frmWin.setTitle(CLIENT_WIN_TITLE + " (" + clientSocket.getLocalAddress() + ") " + username);
    }

    // call when window X pressed
    private void exitClient() {
        int option = JOptionPane.showConfirmDialog(frmWin, "Do you really want to Exit?", "Client Exit", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.OK_OPTION)
            stopClient();
    }

    private void stopClient() {
        clientSocket.close(); // will throw 'SocketException' and unblock I/O. see close() API
    }

    private void closeClient() {
        closeClient("Chat Client Error", "The connection with the Server is LOST!");
    }

    private void closeClient(String title, String reason) {
        if (title != null && reason != null && clientSocket != null && clientSocket.isConnected()) {
            String msg = reason + "\nClient will close now...";
            JOptionPane.showMessageDialog(frmWin, msg, title, JOptionPane.ERROR_MESSAGE);
            stopClient();
        }

        log("Client Closed!");

        // close GUI
        frmWin.dispose();

        // close client
        System.exit(0);
    }

    private void sendMsgToServer() {
        Thread thread = new Thread(() -> {
            clientSocket.writeMessage(filedMsg.getText());
            filedMsg.setText("");
            filedMsg.requestFocusInWindow();
        });
        thread.start();
    }

    private void createClientGUI() {
        frmWin = new JFrame("Chat Client");
        frmWin.setSize(CLIENT_WIN_SIZE);
        frmWin.setAlwaysOnTop(true);
        frmWin.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frmWin.setLocationRelativeTo(null);

        frmWin.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitClient();
            }
        });

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(CLIENT_LOG_FONT);
        areaLog.setMargin(new Insets(5, 5, 5, 5));
        areaLog.setBackground(CLIENT_LOG_BGCOLOR);
        areaLog.setForeground(CLIENT_LOG_FGCOLOR);

        // panel for send message
        JPanel pnlSendMsg = new JPanel(new BorderLayout());
        JLabel lblMsg = new JLabel(" Your DialogMessage: ");

        filedMsg = new JTextField();
        filedMsg.setForeground(Color.BLUE);
        filedMsg.addActionListener(new ActionListener() {
            @Override   // called when ENTER Key was hit
            public void actionPerformed(ActionEvent event) {
                sendMsgToServer();
            }
        });

        btnSend = new JButton("SEND");
        btnSend.addActionListener(new ActionListener() {
            @Override   // called when Mouse Clicked on the BUTTON
            public void actionPerformed(ActionEvent ae) {
                sendMsgToServer();
            }
        });
        pnlSendMsg.add(lblMsg, BorderLayout.WEST);
        pnlSendMsg.add(filedMsg, BorderLayout.CENTER);
        pnlSendMsg.add(btnSend, BorderLayout.EAST);

        frmWin.add(new JScrollPane(areaLog), BorderLayout.CENTER);
        frmWin.add(pnlSendMsg, BorderLayout.SOUTH);
        frmWin.setVisible(true);

        // disable send msg
        setChatMsgSendEnable(false);

        System.out.println("**** createClientGUI() finished! ****");
    }

    private void setChatMsgSendEnable(boolean status) {
        filedMsg.setEnabled(status);
        btnSend.setEnabled(status);

        if (status)
            filedMsg.requestFocusInWindow();
    }

    private void log(String msg) {
        areaLog.append(msg + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }
}
