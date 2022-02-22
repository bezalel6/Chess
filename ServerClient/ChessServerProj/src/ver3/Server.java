package ver3;

import SharedClasses.LoginInfo;
import SharedClasses.messages.ErrorMessage;
import SharedClasses.messages.LoginMessage;
import SharedClasses.messages.Message;
import ver3.server_info.AppSocket;
import ver3.server_info.DB;
import ver3.server_info.players.PlayerNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Server -דוגמה לשרת משחק רשת מרובה משחקים .
 * ---------------------------------------------------------------------------
 * by Ilan Perez (ilanperets@gmail.com)  10/11/2021
 */
public class Server {
    public static final String SERVER_WIN_TITLE = "Chat Server";
    public static final Font SERVER_LOG_FONT = new Font("Consolas", Font.PLAIN, 16); // Font.MONOSPACED
    // constants
    private static final String LOGIN_AS_GUEST = "#LOGIN_AS_GUEST";
    private static final String LOGIN_AS_USER = "#LOGIN_AS_USER";
    private static final String LOGIN_CANCEL = "#LOGIN_CANCEL";
    private static final int SERVER_DEFAULT_PORT = 1234;
    private static final Dimension SERVER_WIN_SIZE = new Dimension(580, 400);
    private static final Color SERVER_LOG_BGCOLOR = Color.BLACK;
    private static final Color SERVER_LOG_FGCOLOR = Color.GREEN;
    // תכונות
    private JFrame frmWin;
    private JTextArea areaLog;

    private String serverIP;
    private int serverPort;
    private boolean serverSetupOK, serverRunOK;
    private ServerSocket serverSocket;
    private int autoChatterID;
    private ArrayList<PlayerNet> netPlayers;


    /**
     * Constructor for ChatServer.
     */
    public Server() {
        createServerGUI();
        setupServer();

        autoChatterID = 0;
        netPlayers = new ArrayList();
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
    public static void main(String args[]) {
        Server chatServer = new Server();
        chatServer.runServer();

        System.out.println("**** ChatServer main() finished! ****");
    }

    // Run the server - wait for clients to connect & handle them
    public void runServer() {
        if (serverSetupOK) {
            String serverAddress = "(" + serverIP + ":" + serverPort + ")";
            log("SERVER" + serverAddress + " Setup & Running!\n");
            frmWin.setTitle(SERVER_WIN_TITLE + " " + serverAddress);

            serverRunOK = true;

            // loop while server running OK
            while (serverRunOK) {
                Socket socketToClient = waitForClient();  // (blocking)

                if (socketToClient == null)
                    serverRunOK = false;
                else
                    handleClient(socketToClient);
            }
        }

        closeServer("runServer() finised!");

        System.out.println("**** runServer() finished! ****");
    }

    // Wait for client to connect.
    // Return the client socket or null if serverSocket closed.
    private Socket waitForClient() {
        Socket socketToClient;

        System.out.println("Waiting for client ...");

        try {
            // Wait for a new client to connect. return client socket.
            socketToClient = serverSocket.accept(); // blocking method
        } catch (IOException exp) {
            socketToClient = null;
        }

        return socketToClient;
    }

    // handle client in a separate thread
    private void handleClient(Socket socketToClient) {
        new Thread(() -> {
            AppSocket playerSocket = new AppSocket(socketToClient);

            LoginMessage loginMessage = loginPlayer(playerSocket);
            if (loginMessage != null) {
                PlayerNet player = login(loginMessage.getLoginInfo(), playerSocket);

                boolean isLoggedIn = player != null;

                while (isLoggedIn) {
                    Message msg = playerSocket.readMessage();
                    if (msg == null) // check if chatter disconnected
                        break;
                    processMsgFromClient(player, msg);
                }
                playerDisconnected(player);
            }
        }).start();
    }

    private LoginMessage loginPlayer(AppSocket playerSocket) {
        askForLogin(playerSocket);
        return readLoginMessage(playerSocket);
    }

    public void askForLogin(AppSocket appSocket) {
        appSocket.writeMessage(new LoginMessage());
    }

    public PlayerNet login(LoginInfo loginInfo, AppSocket appSocket) {
        String playerId = null;

        if (loginInfo.isCancel()) {
//            appSocket.writeMessage("bye bye");
            return null;
        }

        Message responseMessage = null;
        if (loginInfo.isGuest()) {
            playerId = "GUEST#" + autoChatterID++;
            loginInfo.setUsername(playerId);
            responseMessage = new LoginMessage("Welcome " + playerId, loginInfo);
        } else {
            String username = loginInfo.getUsername();
            String password = loginInfo.getPassword();
            System.out.println(LOGIN_AS_USER + ": un=" + username + ", pw=" + password);

            // check un & pw in DB
            try {
                if (DB.isUserExists(username, password)) {
                    if (netPlayers.stream().noneMatch(p -> p.getId().equals(username))) {
                        playerId = username;
                        responseMessage = new LoginMessage("Welcome " + playerId, loginInfo);
                    } else {
                        responseMessage = new ErrorMessage("User already connected");
                    }
                } else {
                    responseMessage = new ErrorMessage("Wrong username or password");
                }
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        }
        appSocket.writeMessage(responseMessage);

        PlayerNet ret;
        if (!(responseMessage instanceof ErrorMessage)) {
            ret = new PlayerNet(appSocket, playerId);
            netPlayers.add(ret);  // add AppSocket object to chattersList
            log("Client(" + appSocket.getRemoteAddress() + ") Connected as " + playerId);
        } else {
            return login(readLoginMessage(appSocket).getLoginInfo(), appSocket);
        }
        return ret;
    }

    private LoginMessage readLoginMessage(AppSocket appSocket) {
        LoginMessage ret = null;
        try {
            ret = ((LoginMessage) appSocket.readMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public void processMsgFromClient(PlayerNet sendingPlayer, Message message) {
        System.out.println("proccessMsgFromClient - message=" + message);
        String msg = message.getSubject();

        log("[" + sendingPlayer.getId() + "]: " + msg);
        broadcastMessage(sendingPlayer, message);


    }

    private void playerDisconnected(PlayerNet playerNet) {
        if (playerNet != null) {
            playerNet.getSocketToClient().writeMessage("bye bye");
            log(playerNet.getId() + " Disconnected!");
            netPlayers.remove(playerNet);
            playerNet.getSocketToClient().close();
        }
    }

    public void broadcastMessage(PlayerNet sendingPlayer, Message msg) {
        // send chatter message to all online chatters
        for (PlayerNet otherClient : netPlayers) {
            String clientId = sendingPlayer.getId();
            if (otherClient == sendingPlayer)
                clientId = "Me";

            otherClient.getSocketToClient().writeMessage("[" + clientId + "]: " + msg.getSubject());
        }
    }

    private void exitServer() {
        int option = JOptionPane.showConfirmDialog(frmWin, "Do you realy want to EXIT ?", "Server Exit", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.OK_OPTION)
            stopServer();
    }

    private void stopServer() {
        try {
            // This will throw cause an Exception on serverSocket.accept() in waitForClient() method
            serverSocket.close();

            // close all threads & clients
            for (int i = 0; i < netPlayers.size(); i++)
                netPlayers.get(i).getSocketToClient().close();

            log("Server Stopped!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void closeServer(String cause) {
        if (serverSocket != null && !serverSocket.isClosed())
            stopServer();

        log("Server Closed!");

        frmWin.dispose(); // close GUI
    }

    // setup Server Address(IP&Port) and create the ServerSocekt
    private void setupServer() {
        try {
            serverPort = -1;
            serverIP = InetAddress.getLocalHost().getHostAddress(); // get Computer IP

            String port = JOptionPane.showInputDialog(frmWin, "Enter Server PORT Number:", SERVER_DEFAULT_PORT);

            if (port == null) // check if Cancel button was pressed
                serverPort = -1;
            else
                serverPort = Integer.parseInt(port);

            // Setup Server Socket ...
            serverSocket = new ServerSocket(serverPort);

            serverSetupOK = true;
        } catch (Exception exp) {
            serverSetupOK = false;
            String serverAddress = serverIP + ":" + serverPort;
            log("Can't setup Server Socket on " + serverAddress + "\n" + "Fix the problem & restart the server.", exp, frmWin);
        }

        System.out.println("**** setupServer() finished! ****");
    }

    // create server GUI
    private void createServerGUI() {
        frmWin = new JFrame();
        frmWin.setSize(SERVER_WIN_SIZE);
        frmWin.setAlwaysOnTop(true);
        frmWin.setTitle(SERVER_WIN_TITLE);
        frmWin.setLocationRelativeTo(null);
        frmWin.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frmWin.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitServer();
            }
        });

        // create displayArea
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(SERVER_LOG_FONT);
        areaLog.setMargin(new Insets(5, 5, 5, 5));
        areaLog.setBackground(SERVER_LOG_BGCOLOR);
        areaLog.setForeground(SERVER_LOG_FGCOLOR);

        areaLog.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                    Font font = areaLog.getFont();
                    Font biggerFont = new Font(font.getFamily(), font.getStyle(), font.getSize() + 1);
                    areaLog.setFont(biggerFont);
                }
                if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                    Font font = areaLog.getFont();
                    Font smallerFont = new Font(font.getFamily(), font.getStyle(), font.getSize() - 1);
                    areaLog.setFont(smallerFont);
                }
            }
        });

        // panel for send message
        JPanel pnlActions = new JPanel(new BorderLayout());

        JLabel lblMsg = new JLabel(" Message: ");
        JTextField filedMsg = new JTextField("");
        filedMsg.setForeground(Color.BLUE);
        filedMsg.addActionListener(new ActionListener() {
            @Override   // called when ENTER Key was hit
            public void actionPerformed(ActionEvent event) {
                //sendMsgToAllClients(msgField.getText());
                System.out.println("sendMsgToAllClients: " + filedMsg.getText());
            }
        });

        JPanel pnlButtons = new JPanel(new FlowLayout(0, 2, 1));
        JButton btnSend = new JButton("SEND ALL");
        btnSend.addActionListener(new ActionListener() {
            @Override   // called when Mouse Clicked on the BUTTON
            public void actionPerformed(ActionEvent ae) {
                System.out.println("sendMsgToAllClients: " + filedMsg.getText());
                //sendMsgToAllClients(msgField.getText());
            }
        });

        JButton btnClear = new JButton("CLEAR");
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                areaLog.setText("");
            }
        });
        JButton btnOnline = new JButton("ONLINE");
        btnOnline.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                showOnlineChatters();
            }
        });
        pnlButtons.add(btnSend);
        pnlButtons.add(btnOnline);
        pnlButtons.add(btnClear);

        pnlActions.add(lblMsg, BorderLayout.WEST);
        pnlActions.add(filedMsg, BorderLayout.CENTER);
        pnlActions.add(pnlButtons, BorderLayout.EAST);

        // add all components to window
        frmWin.add(new JScrollPane(areaLog), BorderLayout.CENTER);
        frmWin.add(pnlActions, BorderLayout.SOUTH);

        // show window
        frmWin.setVisible(true);

        System.out.println("**** createServerGUI() finished! ****");
    }

    private void showOnlineChatters() {
        String online = "\n" + netPlayers.size() + " Chatter(s) Online";
        if (netPlayers.isEmpty())
            online += "!\n";
        else
            online += ":\n";
        for (int i = 0; i < netPlayers.size(); i++)
            online += "> " + netPlayers.get(i).getId() + "\n";
        log(online);
    }

    private void log(String msg) {
        areaLog.append(msg + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
        System.out.println(msg);
    }
}
