package ver5.server;

import ver5.SharedClasses.*;
import ver5.SharedClasses.messages.Message;
import ver5.SharedClasses.messages.MessageType;
import ver5.SharedClasses.messages.SyncedListType;
import ver5.SharedClasses.networking.AppSocket;
import ver5.SharedClasses.ui.windows.CloseConfirmationJFrame;
import ver5.SyncedList;
import ver5.server.DB.DB;
import ver5.server.game.GameSession;
import ver5.server.players.Player;
import ver5.server.players.PlayerAI;
import ver5.server.players.PlayerNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Server -דוגמה לשרת משחק רשת מרובה משחקים .
 * ---------------------------------------------------------------------------
 * by Ilan Perez (ilanperets@gmail.com)  10/11/2021
 */
public class Server {
    public static final String SERVER_WIN_TITLE = "Chat Server";
    public static final Font SERVER_LOG_FONT = new Font("Consolas", Font.PLAIN, 16); // Font.MONOSPACED
    // constants
    private static final int SERVER_DEFAULT_PORT = 1234;
    private static final Dimension SERVER_WIN_SIZE = new Dimension(580, 400);
    private static final Color SERVER_LOG_BGCOLOR = Color.BLACK;
    private static final Color SERVER_LOG_FGCOLOR = Color.GREEN;
    private final ArrayList<PlayerNet> netPlayers;
    private final ArrayList<GameSession> gameSessions;
    private final SyncedList<String, SavedGame> gamePool;
    // תכונות
    private CloseConfirmationJFrame frmWin;
    private JTextArea areaLog;
    private String serverIP;
    private int serverPort;
    private boolean serverSetupOK, serverRunOK;
    private MyServerSocket serverSocket;
    private int autoChatterID;

    /**
     * Constructor for ChatServer.
     */
    public Server() {
        createServerGUI();
        setupServer();

        autoChatterID = 0;
        netPlayers = new ArrayList<>();
        gameSessions = new ArrayList<>();
        gamePool = new SyncedList<>(this, SyncedListType.JOINABLE);
    }

    // create server GUI
    private void createServerGUI() {
        frmWin = new CloseConfirmationJFrame(this::exitServer) {{
            setSize(SERVER_WIN_SIZE);
            setAlwaysOnTop(false);
            setTitle(SERVER_WIN_TITLE);
        }};

        // create displayArea
        areaLog = new JTextArea() {{
            setEditable(false);
            setFont(SERVER_LOG_FONT);
            setMargin(new Insets(5, 5, 5, 5));
            setBackground(SERVER_LOG_BGCOLOR);
            setForeground(SERVER_LOG_FGCOLOR);
            addKeyListener(new KeyAdapter() {
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
        }};
        // panel for send message
        JPanel pnlActions = new JPanel(new BorderLayout());

        JLabel lblMsg = new JLabel(" Message: ");
        JTextField filedMsg = new JTextField() {{
            setForeground(Color.BLUE);
            addActionListener(event -> {
            });
        }};

        JPanel pnlButtons = new JPanel(new FlowLayout(0, 2, 1));

        pnlActions.add(lblMsg, BorderLayout.WEST);
        pnlActions.add(filedMsg, BorderLayout.CENTER);
        pnlActions.add(pnlButtons, BorderLayout.EAST);

        // add all components to window
        frmWin.add(new JScrollPane(areaLog), BorderLayout.CENTER);
        frmWin.add(pnlActions, BorderLayout.SOUTH);

        // show window
        frmWin.setLocationRelativeTo(null);
        frmWin.setVisible(true);
//        ShowOnScreen.showOnScreen(2, frmWin);
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
            serverSocket = new MyServerSocket(serverPort);

            serverSetupOK = true;
        } catch (Exception exp) {
            serverSetupOK = false;
            String serverAddress = serverIP + ":" + serverPort;
            log("Can't setup Server Socket on " + serverAddress + "\n" + "Fix the problem & restart the server.", exp, frmWin);
        }

        System.out.println("**** setupServer() finished! ****");
    }

    private void exitServer() {
        closeServer("");
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

    private void closeServer(String cause) {
        if (serverSocket != null && !serverSocket.isClosed())
            stopServer();

        log("Server Closed!");
        serverRunOK = false;
        frmWin.dispose(); // close GUI
    }

    private void stopServer() {
        try {
            // This will throw cause an Exception on serverSocket.accept() in waitForClient() method
            serverSocket.close();

            // close all threads & clients
            netPlayers.forEach(netPlayer -> netPlayer.disconnect("Server Closing"));

            log("Server Stopped!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void log(String msg) {
        msg = StrUtils.uppercaseFirstLetters(msg);
        areaLog.append(msg + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
        System.out.println(msg);
    }

    // main
    public static void main(String[] args) {
        Server chatServer = new Server();
        chatServer.runServer();

        System.out.println("**** ChatServer main() finished! ****");
    }

    // Run the server - wait for clients to connect & handle them
    public void runServer() {
        if (serverSetupOK) {
            String serverAddress = "(" + serverIP + ":" + serverPort + ")";
            log("SERVER" + serverAddress + " Setup & Running!");
//            log(new NoThrow<String>(DB::getAllUsersCredentials).get());
            frmWin.setTitle(SERVER_WIN_TITLE + " " + serverAddress);

            serverRunOK = true;

            // loop while server running OK
            while (serverRunOK) {
                AppSocket appSocketToPlayer = serverSocket.acceptAppSocket();
                if (appSocketToPlayer == null)
                    serverRunOK = false;
                else
                    handleClient(appSocketToPlayer);
            }
        }

        closeServer("runServer() finised!");

        System.out.println("**** runServer() finished! ****");
    }


    // handle client in a separate thread
    private void handleClient(AppSocket playerSocket) {
        new Thread(() -> {
            playerSocket.getAliveSocket().start();
            BackgroundSocketHandler backgroundSocketHandler = new BackgroundSocketHandler(playerSocket.getBackgroundSocket(), this);
            backgroundSocketHandler.start();
            Message loginMessage = loginPlayer(playerSocket);
            if (loginMessage == null) {
                return;
            }

            LoginInfo loginInfo = loginMessage.getLoginInfo();
            PlayerNet player = login(loginInfo, playerSocket);

            if (player == null)
                return;
            backgroundSocketHandler.setPlayer(player);
            netPlayers.add(player);
            log("Client(" + playerSocket.getRemoteAddress() + ") Connected as " + loginInfo.getUsername());

            gameSetup(player);
        }).start();
    }


    public void syncList(SyncedList list) {
        netPlayers.forEach(playerNet -> playerNet.getSocketToClient().getBackgroundSocket().writeMessage(Message.syncList(list.syncedListType, list.values())));
    }

    public void gameSetup(PlayerNet player) {
        SavedGame[] joinable = getJoinableGames(player);
        SavedGame[] resumable = getResumableGames(player);

        GameSettings gameSettings = player.getGameSettings(joinable, resumable);
        if (gameSettings == null)
            return;
        switch (gameSettings.getGameType()) {
            case CREATE_NEW -> {
                if (gameSettings.isVsAi()) {
                    startGameVsAi(player, gameSettings);
                } else {
                    String id = createGameID();
                    SavedGame savedGame = new SavedGame(id, player.getUsername(), gameSettings);
                    gamePool.put(id, savedGame);
                    player.waitForMatch();
                }
            }
            case JOIN_EXISTING -> {
                SavedGame savedGame = gamePool.get(gameSettings.getJoiningToGameID());
                GameSession gameSession = new GameSession(savedGame, getPlayerNet(savedGame.creatorUsername), player, this);
                gameSessions.add(gameSession);
                gameSession.start();
                gamePool.remove(gameSettings.getJoiningToGameID());
            }
            case RESUME -> {
                startGameVsAi(player, gameSettings);
            }
        }

    }

    private PlayerNet getPlayerNet(String username) {
        return netPlayers.stream()
                .filter(p -> p.getUsername().equals(username))
                .findAny()
                .orElse(null);
    }

    private void startGameVsAi(PlayerNet player, GameSettings gameSettings) {
        String resumingId = gameSettings.getResumingGameID();
        GameSession gameSession;
        if (resumingId == null) {
            PlayerAI ai = PlayerAI.createPlayerAi(gameSettings.getAiParameters());
            gameSession = new GameSession(createGameID(), player, ai, gameSettings, this);
        } else {
            SavedGame resuminGame = null;
            try {
                resuminGame = DB.loadUnfinishedGameByGameId(resumingId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            assert resuminGame != null;
            PlayerAI ai = PlayerAI.createPlayerAi(resuminGame.gameSettings.getAiParameters());
            gameSession = new GameSession(resuminGame, player, ai, this);
        }
        gameSessions.add(gameSession);
        gameSession.start();
    }

    public SavedGame[] getResumableGames(PlayerNet playerNet) {
        try {
            return DB.getUnfinishedGames(playerNet.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new SavedGame[0];
    }

    private String createGameID() {
        String ret;
        do {
            ret = (Math.abs(new Random().nextInt()) + "").substring(0, 4);
        } while (!canCreateGameID(ret));
        return ret;
    }

    private boolean canCreateGameID(String id) {
        if (gamePool.containsKey(id))
            return false;
        try {
            return !DB.isGameIdExists(id);
        } catch (SQLException e) {
            return false;
        }
    }

    private Message loginPlayer(AppSocket playerSocket) {
        askForLogin(playerSocket);
        return readLoginMessage(playerSocket);
    }

    public void askForLogin(AppSocket appSocket) {
        appSocket.writeMessage(Message.askForLogin());
    }

    public PlayerNet login(LoginInfo loginInfo, AppSocket appSocket) {
        Message responseMessage = null;
        try {
            responseMessage = responseToLogin(loginInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assert responseMessage != null;
        appSocket.writeMessage(responseMessage);

        if (loginInfo.getLoginType() == LoginType.CANCEL)
            return null;

        if (responseMessage.getMessageType() != MessageType.ERROR) {
            PlayerNet playerNet = new PlayerNet(appSocket, loginInfo.getLoginType() == LoginType.GUEST, loginInfo.getUsername());
            playerNet.getSocketToClient().getAliveSocket().addOnDisconnect(() -> disconnectPlayer(playerNet, "player disconnected"));
            return playerNet;
        }
        return login(readLoginMessage(appSocket).getLoginInfo(), appSocket);
    }
//    private Message

    private Message responseToLogin(LoginInfo loginInfo) throws SQLException {
        String username = loginInfo.getUsername();
        String password = loginInfo.getPassword();

        return switch (loginInfo.getLoginType()) {
            case LOGIN -> {
                if (DB.isUserExists(username, password)) {
                    if (!isUserConnected(username)) {
                        yield Message.welcomeMessage("Welcome " + username, loginInfo);
                    } else {
                        yield Message.error("User already connected");
                    }
                } else {
                    yield Message.error("Wrong username or password");
                }
            }
            case GUEST -> {
                loginInfo.setUsername(RegEx.Prefixes.GUEST_PREFIX + "#" + autoChatterID++);
                yield Message.welcomeMessage("Welcome " + loginInfo.getUsername(), loginInfo);
            }
            case CANCEL -> Message.bye("");
            case REGISTER -> {
                if (DB.isUsernameExists(username)) {
                    yield Message.error("Username Exists Already");
                } else {
                    DB.addUser(username, password);
                    yield Message.welcomeMessage("Welcome " + username, loginInfo);
                }
            }
        };
    }

    private boolean isUserConnected(String username) {
        return netPlayers.stream().anyMatch(p -> p.getUsername().equals(username));
    }

    //todo get rid of this func
    private Message readLoginMessage(AppSocket appSocket) {
        Message msg = appSocket.readMessage();
        return msg;
    }

    public void disconnectPlayer(Player player, String cause) {
        if (player != null) {
            log("disconnected " + player.getUsername() + cause);
            player.disconnect(cause);

            if (player instanceof PlayerNet) {
                netPlayers.remove(player);
            }
            List<SavedGame> del = gamePool.values()
                    .stream()
                    .filter(game -> game.creatorUsername.equals(player.getUsername()))
                    .toList();
            del.forEach(deleting -> gamePool.remove(deleting.gameId));
        }
    }

    public SavedGame[] getJoinableGames(PlayerNet playerNet) {
        return gamePool.values().toArray(new SavedGame[0]);
    }

}
