package ver14;

import ver14.DB.DB;
import ver14.Model.minimax.Minimax;
import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.SavedGames.CreatedGame;
import ver14.SharedClasses.Game.SavedGames.GameInfo;
import ver14.SharedClasses.Game.SavedGames.UnfinishedGame;
import ver14.SharedClasses.IDsGenerator;
import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.LoginType;
import ver14.SharedClasses.RegEx;
import ver14.SharedClasses.Sync.SyncableItem;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.SharedClasses.Threads.ErrorHandling.*;
import ver14.SharedClasses.Threads.ThreadsManager;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.messages.Message;
import ver14.SharedClasses.messages.MessageType;
import ver14.SharedClasses.networking.AppSocket;
import ver14.SharedClasses.ui.MyJButton;
import ver14.SharedClasses.ui.windows.MyJFrame;
import ver14.game.GameSession;
import ver14.players.Player;
import ver14.players.PlayerAI.PlayerAI;
import ver14.players.PlayerNet.PlayerNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The type Server.
 */
public class Server implements ErrorContext, EnvManager {
    /**
     * The constant SERVER_WIN_TITLE.
     */
    public static final String SERVER_WIN_TITLE = "Chess Server";
    /**
     * The constant SERVER_LOG_FONT.
     */
    public static final Font SERVER_LOG_FONT = new Font("Consolas", Font.PLAIN, 16); // Font.MONOSPACED
    // constants
    private static final int SERVER_DEFAULT_PORT = 1234;
    private static final Dimension SERVER_WIN_SIZE = new Dimension(580, 400);
    private static final Color SERVER_LOG_BGCOLOR = Color.BLACK;
    private static final Color SERVER_LOG_FGCOLOR = Color.GREEN;
    private final static IDsGenerator gameIDGenerator;
    private static int START_AT_PORT = -1;

    static {

        gameIDGenerator = new IDsGenerator() {
            @Override
            public boolean canUseId(String id) {
                if (!super.canUseId(id))
                    return false;
                return !DB.isGameIdExists(id);
            }
        };
    }

    private SyncedItems<PlayerNet> players;
    private SyncedItems<GameSession> gameSessions;
    private SyncedItems<GameInfo> gamePool;
    private ArrayList<SyncedItems<?>> syncedLists;
    private MyJFrame frmWin;
    private JTextArea areaLog;
    private String serverIP;
    private int serverPort;
    private boolean serverSetupOK, serverRunOK;
    private MyServerSocket serverSocket;
    private int autoGuestID;

    /**
     * Constructor for ChatServer.
     */
    public Server() {
        ErrorManager.setEnvManager(this);
        ThreadsManager.handleErrors(() -> {
            createServerGUI();
            setupServer();
        });

    }

    // create server GUI
    private void createServerGUI() {
        frmWin = new MyJFrame() {{
            setSize(SERVER_WIN_SIZE);
            setTitle(SERVER_WIN_TITLE);
            setOnExit(Server.this::exitServer);
//            setAlwaysOnTop(true);
        }};
        JPanel bottomPnl = new JPanel();
        MyJButton connectedUsersBtn = new MyJButton("Connected Users", () -> {
            log("Connected Users:");
            players.forEachItem(plr -> {
                log(StrUtils.dontCapWord(plr.getUsername()));
            });
        });
        MyJButton gameSessionsBtn = new MyJButton("Game Sessions", () -> {
            log("Game Sessions:");
            gameSessions.forEachItem(session -> {
                log(session.sessionsDesc());
            });
        });
        MyJButton usersDetailsBtn = new MyJButton("Users Details", () -> {
            log("Users Details:");
            DB.getAllUserDetails().forEach(userDetails -> log(StrUtils.dontCapFull(userDetails.toString())));
        });

        bottomPnl.add(connectedUsersBtn);
        bottomPnl.add(gameSessionsBtn);
        bottomPnl.add(usersDetailsBtn);
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
        frmWin.add(new JScrollPane(areaLog), BorderLayout.CENTER);
        frmWin.add(bottomPnl, BorderLayout.SOUTH);

        // show window
        frmWin.setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> {
            frmWin.setVisible(true);
        });
    }

    // setup Server Address(IP&Port) and create the ServerSocket
    private void setupServer() {
        try {
            autoGuestID = 0;
            players = new SyncedItems<>(SyncedListType.CONNECTED_USERS, this::syncedListUpdated);
            gamePool = new SyncedItems<>(SyncedListType.JOINABLE_GAMES, this::syncedListUpdated);
            gameSessions = new SyncedItems<>(SyncedListType.ONGOING_GAMES, this::syncedListUpdated);

            this.syncedLists = new ArrayList<>() {{
                add(players);
                add(gamePool);
                add(gameSessions);
            }};
            serverPort = START_AT_PORT;
            serverIP = InetAddress.getLocalHost().getHostAddress(); // get Computer IP

            if (serverPort == -1) {
                String port = System.getenv("PORT");
                if (port == null)
                    port = JOptionPane.showInputDialog(frmWin, "Enter Server PORT Number:", SERVER_DEFAULT_PORT);

                if (port == null) // check if Cancel button was pressed
                    serverPort = -1;
                else
                    serverPort = Integer.parseInt(port);
            }

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

    /**
     * Log.
     *
     * @param msg the msg
     */
    public void log(String msg) {
        msg = StrUtils.format(msg);
        areaLog.append(msg + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
        System.out.println(msg);
    }

    /**
     * Synced list updated.
     *
     * @param list the list
     */
    public void syncedListUpdated(SyncedItems<?> list) {
        list = prepareListForSend(list);
        if (list.isEmpty())
            return;
        SyncedItems<?> finalList = list;
        players.forEachItem(playerNet -> playerNet.getSocketToClient().writeMessage(Message.syncLists(finalList)));
    }

    /**
     * Log.
     *
     * @param msg the msg
     * @param ex  the ex
     * @param win the win
     */
    public static void log(String msg, Exception ex, JFrame... win) {
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
        players.forEachItem(player -> {
            ErrorHandler.ignore(() -> {
                player.disconnect(cause);
            });
        });
        if (serverSocket != null && !serverSocket.isClosed())
            stopServer();

        log("Server Closed! " + cause);
        serverRunOK = false;
        frmWin.dispose(); // close GUI

//        😨
        ThreadsManager.stopAll();
    }

    //todo move to synceditems as a func
    private SyncedItems<?> prepareListForSend(SyncedItems<?> list) {
        SyncedItems<?> ret = new SyncedItems<>(list.syncedListType);
        ret.addAll(list.stream().map(SyncableItem::getSyncableItem).collect(Collectors.toList()));
        return ret.clean();
    }

    private void stopServer() {
        try {
            // This will throw cause an Exception on serverSocket.accept() in waitForClient() method
            serverSocket.close();

            // close all threads & clients
            players.forEachItem(netPlayer -> (netPlayer).disconnect("Server Closing"));

            log("Server Stopped!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * The entry point of the application.
     *
     * @param args the input arguments
     */
// main
    public static void main(String[] args) {
        if (args.length > 0) {
            String port = Arrays.stream(args).filter(str -> str.startsWith("p=")).findAny().orElse(null);
            if (port != null)
                try {
                    START_AT_PORT = Integer.parseInt(port.substring(port.indexOf('=') + 1));
                } catch (NumberFormatException e) {
                    START_AT_PORT = -1;
                }
            Minimax.SHOW_UI = Arrays.stream(args).anyMatch(str -> str.equalsIgnoreCase("DEBUG_MINIMAX"));
        }
        Server server = new Server();
        server.runServer();

        System.out.println("**** ChessServer main() finished! ****");
    }

    private void sendAllSyncedLists(PlayerNet player, SyncedItems<?>... excludeLists) {
        ArrayList<SyncedItems<?>> lists = new ArrayList<>();
        for (SyncedItems<?> syncedList : syncedLists) {
            if (Arrays.stream(excludeLists).noneMatch(excludedList -> excludedList.equals(syncedList)))
                lists.add(prepareListForSend(syncedList));
        }
        player.getSocketToClient().writeMessage(Message.syncLists(lists.toArray(new SyncedItems[0])));
    }

    /**
     * Run server.
     */
// Run the server - wait for clients to connect & handle them
    public void runServer() {
        ThreadsManager.HandledThread.runInHandledThread(() -> {
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
                    else {
                        handleClient(appSocketToPlayer);
                    }
                }
                closeServer("runServer() finised!");
            } else {
                closeServer("server setup was not ok");
            }
            System.out.println("**** runServer() finished! ****");
        });


    }

    // handle client in a separate thread
    private void handleClient(AppSocket playerSocket) {
        ThreadsManager.HandledThread.runInHandledThread(() -> {
            ServerMessagesHandler messagesHandler = new ServerMessagesHandler(this, playerSocket);
            playerSocket.setMessagesHandler(messagesHandler);
            playerSocket.start();
            PlayerNet player = login(playerSocket);
            if (player == null) {
                playerSocket.stopReading();
                return;
            }

            messagesHandler.setPlayer(player);
            players.add(player);

            sendAllSyncedLists(player, players);

            log("Client(" + playerSocket.getRemoteAddress() + ") Connected as " + player.getLoginInfo().getUsername());

            gameSetup(player);

        });
    }

    /**
     * Login player net.
     *
     * @param appSocket the app socket
     * @return the player net
     */
    public PlayerNet login(AppSocket appSocket) {
        Message request = appSocket.requestMessage(Message.askForLogin());
        Message responseMessage = responseToLogin(request.getLoginInfo());
        if (responseMessage == null)
            return null;
        while (responseMessage.getMessageType() == MessageType.ERROR) {

            responseMessage.setRespondingTo(request);
            request = appSocket.requestMessage(responseMessage);

            responseMessage = responseToLogin(request.getLoginInfo());

            if (responseMessage == null)
                return null;
        }

        appSocket.respond(responseMessage, request);

        LoginInfo loginInfo = request.getLoginInfo();

        if (loginInfo.getLoginType() == LoginType.CANCEL)
            return null;

        if (responseMessage.getMessageType() != MessageType.ERROR) {
            return new PlayerNet(appSocket, loginInfo);
        }
        return login(appSocket);
    }

    private Message responseToLogin(LoginInfo loginInfo) {
        if (loginInfo == null)
            return null;
        String username = loginInfo.getUsername();
        String password = loginInfo.getPassword();

        return switch (loginInfo.getLoginType()) {
            case LOGIN -> {
                if (DB.isUserExists(username, password)) {
                    if (!isLoggedIn(username)) {
                        yield Message.welcomeMessage("Welcome " + username, loginInfo);
                    } else {
                        yield Message.error("User already connected");
                    }
                } else {
                    yield Message.error("Wrong username or password");
                }
            }
            case GUEST -> {
                loginInfo.setUsername(RegEx.Prefixes.GUEST_PREFIX + "#" + autoGuestID++);
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

    private boolean isLoggedIn(String username) {
        return players.stream().anyMatch(p -> p.getUsername().equals(username));
    }

    /**
     * End of game session.
     *
     * @param session the session
     */
    public void endOfGameSession(GameSession session) {
        gameSessions.remove(session.gameID);
        (session.getPlayers()).stream().parallel().forEach(player -> {
            if (player.isConnected()) {
                gameSetup(player);
            }
        });
    }

    /**
     * Game setup.
     *
     * @param player the player
     */
    public void gameSetup(Player player) {

        SyncedItems<GameInfo> joinable = getJoinableGames(player);
        SyncedItems<GameInfo> resumable = getResumableGames(player);

        GameSettings gameSettings = null;
        try {
            gameSettings = player.getGameSettings(joinable, resumable);
        } catch (MyError.DisconnectedError e) {
        }
        if (gameSettings == null) {
            playerDisconnected(player);
            return;
        }
//        if (gameSettings == null) {
//            playerDisconnected(player);
//            return;
//        }
        switch (gameSettings.getGameType()) {
            case CREATE_NEW -> {
                if (gameSettings.isVsAi()) {
                    startGameVsAi(player, gameSettings);
                } else {
                    String id = gameIDGenerator.generate();
                    CreatedGame gameInfo = new CreatedGame(id, player.getUsername(), gameSettings);
                    gamePool.put(id, gameInfo);
                    player.waitForMatch();
                }
            }
            case JOIN_EXISTING -> {
                GameInfo gameInfo = gamePool.get(gameSettings.getGameID());
                if (gameInfo == null) {
//                    throw new Error("user should get an error");
                    player.error("game does not exist");
                } else {
                    GameSession gameSession = new GameSession(gameInfo, getPlayerNet(gameInfo.creatorUsername), player, this);
                    gameSession.start();
                    gameSessions.add(gameSession);
                    gamePool.remove(gameSettings.getGameID());
                }
            }
            case RESUME -> {
                startGameVsAi(player, gameSettings);
            }
        }

    }

    /**
     * Gets joinable games.
     *
     * @param player the player net
     * @return the joinable games
     */
    public SyncedItems<GameInfo> getJoinableGames(Player player) {

        return gamePool.clean();
    }

    /**
     * Gets resumable games.
     *
     * @param player the player
     * @return the resumable games
     */
    public SyncedItems<GameInfo> getResumableGames(Player player) {
        return DB.getUnfinishedGames(player.getUsername()).clean();
    }

    /**
     * Player disconnected.
     *
     * @param player the player
     */
    public void playerDisconnected(Player player) {
        if (player == null)
            return;

        log(player.getUsername() + " disconnected");
        player.disconnect("");

//        Game ongoingGame = player.getOnGoingGame();
//        if (ongoingGame != null) {
//            ongoingGame.playerDisconnected(player);
//        }

        players.remove(player.getUsername());
        List<GameInfo> del = gamePool.values()
                .stream()
                .filter(game -> game.creatorUsername.equals(player.getUsername()))
                .toList();
        del.forEach(deleting -> gamePool.remove(deleting.gameId));
    }

    private void startGameVsAi(Player player, GameSettings gameSettings) {
        GameSession gameSession;
        if (gameSettings.getGameType() == GameSettings.GameType.CREATE_NEW) {
            PlayerAI ai = PlayerAI.createPlayerAi(gameSettings.getAiParameters());
            gameSession = new GameSession(gameIDGenerator.generate(), player, ai, gameSettings, this);
        } else {
            String resumingId = gameSettings.getGameID();
            UnfinishedGame resumingGame = DB.loadUnfinishedGame(resumingId);
            DB.deleteUnfinishedGame(resumingGame);
            PlayerAI ai = PlayerAI.createPlayerAi(resumingGame.gameSettings.getAiParameters());
            gameSession = new GameSession(resumingGame, player, ai, this);
        }
        gameSessions.add(gameSession);
        gameSession.start();
    }

    private PlayerNet getPlayerNet(String username) {
        return players.stream()
                .filter(p -> (p).getUsername().equals(username))
                .findAny()
                .orElse(null);
    }

    /**
     * Create username suggestions array list.
     *
     * @param username the username
     * @return the array list
     */
    public ArrayList<String> createUsernameSuggestions(String username) {

        return UsernameSuggestions.createSuggestions(username);
    }

    /**
     * Context type my error . context type.
     *
     * @return the my error . context type
     */
    @Override
    public ContextType contextType() {
        return null;
//        return MyError.ContextType.Server;
    }

    /**
     * Handled err.
     *
     * @param err the err
     */
    @Override
    public void handledErr(MyError err) {
        log("handled: " + err.getHandledStr());

    }

    /**
     * Critical err.
     *
     * @param err the err
     */
    @Override
    public void criticalErr(MyError err) {
        closeServer("critical error: " + err);
    }
}
