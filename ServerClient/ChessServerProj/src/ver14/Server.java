package ver14;

import ver14.DB.DB;
import ver14.Game.GameSession;
import ver14.Model.Eval.Book;
import ver14.Model.Minimax.Minimax;
import ver14.Players.Player;
import ver14.Players.PlayerAI.PlayerAI;
import ver14.Players.PlayerNet.PlayerNet;
import ver14.SharedClasses.DBActions.DBRequest.DBRequest;
import ver14.SharedClasses.DBActions.DBResponse.DBResponse;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.GameSetup.GameType;
import ver14.SharedClasses.Game.SavedGames.CreatedGame;
import ver14.SharedClasses.Game.SavedGames.GameInfo;
import ver14.SharedClasses.Game.SavedGames.UnfinishedGame;
import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.Login.LoginType;
import ver14.SharedClasses.Misc.IDsGenerator;
import ver14.SharedClasses.Networking.AppSocket;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.SharedClasses.Networking.Messages.MessageType;
import ver14.SharedClasses.Sync.SyncableItem;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.SharedClasses.Threads.ErrorHandling.EnvManager;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorHandler;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Threads.HandledThread;
import ver14.SharedClasses.Threads.MyThread;
import ver14.SharedClasses.Threads.ThreadsManager;
import ver14.SharedClasses.UI.Buttons.MyJButton;
import ver14.SharedClasses.UI.MyJFrame;
import ver14.SharedClasses.Utils.ArgsUtil;
import ver14.SharedClasses.Utils.RegEx;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Server - represents a chess server
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Server implements EnvManager {
    /**
     * The constant SERVER_WIN_TITLE.
     */
    public static final String SERVER_WIN_TITLE = "Chess Server";
    /**
     * The constant SERVER_LOG_FONT.
     */
    public static final Font SERVER_LOG_FONT = new Font("Consolas", Font.PLAIN, 16); // Font.MONOSPACED
    /**
     * the default server port
     */
    private static final int SERVER_DEFAULT_PORT = 1234;
    /**
     * the server window size
     */
    private static final Dimension SERVER_WIN_SIZE = new Dimension(580, 400);
    /**
     * the server log backgroung color
     */
    private static final Color SERVER_LOG_BGCOLOR = Color.BLACK;
    /**
     * the server log foreground color
     */
    private static final Color SERVER_LOG_FGCOLOR = Color.GREEN;
    /**
     * the game ids generator
     */
    private final static IDsGenerator gameIDGenerator;
    /**
     * the port to start with. default is nothing unless one is passed through to
     */
    private static int START_AT_PORT = -1;

    static {

        gameIDGenerator = new IDsGenerator() {
            @Override
            public boolean canUseId(String id) {
                return super.canUseId(id) && !DB.isGameIdExists(id);
            }
        };
    }

    /**
     * all connected clients' app sockets
     */
    private final ArrayList<AppSocket> appSockets = new ArrayList<>();
    /**
     * an atomic reference holding a player waiting for a quick match
     */
    private final AtomicReference<Player> waitingPlayer = new AtomicReference<>();
    /**
     * The Is closing.
     */
    private boolean isClosing = false;
    /**
     * synchronized list of the players
     */
    private SyncedItems<PlayerNet> players;
    /**
     * synchronized list of the gamed sessions
     */
    private SyncedItems<GameSession> gameSessions;
    /**
     * synchronized list of the game pool
     */
    private SyncedItems<GameInfo> gamePool;
    /**
     * list of all the synchronized lists
     */
    private ArrayList<SyncedItems<?>> syncedLists;
    /**
     * the server's window
     */
    private MyJFrame frmWin;
    /**
     * the server's area log
     */
    private JTextArea areaLog;
    /**
     * the server's ip
     */
    private String serverIP;
    /**
     * the server's port
     */
    private int serverPort;
    /**
     * was the server setup ok
     */
    private boolean serverSetupOK;
    /**
     * is the server run ok
     */
    private boolean serverRunOK;
    /**
     * the server socket
     */
    private MyServerSocket serverSocket;
    /**
     * an incrementing value of the connected guests. used to create ids for each connected guest
     */
    private int autoGuestID;

    /**
     * Constructor for ChessServer.
     */
    public Server() {
        MyThread.setEnvManager(this);
        ThreadsManager.handleErrors(() -> {
            createServerGUI();
            setupServer();
        });

    }

    /**
     * create server GUI
     */
    private void createServerGUI() {
        frmWin = new MyJFrame() {{
            setSize(SERVER_WIN_SIZE);
            setTitle(SERVER_WIN_TITLE);
            setOnExit(new StringClosing() {
                @Override
                public String initialValue() {
                    return "Closing Server For maintenance";
                }

                @Override
                public void closing(String val) {
                    exitServer(val);
                }
            });

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
        frmWin.add(new JScrollPane(areaLog), BorderLayout.CENTER);
        frmWin.add(bottomPnl, BorderLayout.SOUTH);

        // show window
        frmWin.setLocationRelativeTo(null);
//        SwingUtilities.invokeLater(() -> {
        frmWin.setVisible(true);
//        });
    }

    /**
     * Sets server.
     */
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

                JTextField portField = new JTextField(SERVER_DEFAULT_PORT + "", 30);
                JTextField threadsField = new JTextField(Minimax.NUMBER_OF_THREADS + "", 30);
                Object[] messageObj = {"Enter Server PORT Number:", portField, "Enter Number of threads for the minimax:", threadsField};
                int dialog = JOptionPane.showConfirmDialog(frmWin, messageObj, "Server Setup", JOptionPane.OK_CANCEL_OPTION);
                if (dialog == JOptionPane.CANCEL_OPTION)
                    return;
                serverPort = Integer.parseInt(portField.getText());
                Minimax.NUMBER_OF_THREADS = Integer.parseInt(threadsField.getText());
                System.out.println("threads = " + Minimax.NUMBER_OF_THREADS);
            }

            // Setup Server Socket ...
            serverSocket = new MyServerSocket(serverPort);

            serverSetupOK = true;
        } catch (Exception exp) {
            serverSetupOK = false;
            String serverAddress = serverIP + ":" + serverPort;
            log("Can't setup Server Socket on " + serverAddress + "\n" + "Fix the problem & restart the server.", exp, frmWin);
        }

    }

    /**
     * Exit server.
     *
     * @param cause the cause
     */
    private void exitServer(String cause) {
        closeServer(cause);
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
     * notifies all players of a change in a synchronized list.
     *
     * @param list the list
     */
    public void syncedListUpdated(SyncedItems<?> list) {
        list = prepareListForSend(list);
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

    /**
     * Close server.
     *
     * @param cause the cause
     */
    private synchronized void closeServer(String cause) {
        if (isClosing)
            return;
        isClosing = true;
        if (gameSessions != null) {
            gameSessions.forEachItem(session -> {
                session.serverStop(cause);
            });
        }
//
//        if (players != null)
//            players.forEachItem(player -> {
//                ErrorHandler.ignore(() -> {
//                    player.disconnect(cause, false);
//                });
//            });

        for (var socket : appSockets) {
            socket.writeMessage(Message.bye(cause));
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            ErrorHandler.ignore(() -> {
                serverSocket.close();
            });
        }

        log("Server Closed! " + cause);
        serverRunOK = false;
        if (frmWin != null)
            SwingUtilities.invokeLater(() -> {
                frmWin.setVisible(false);
                frmWin.dispose(); // close GUI
            });

        ThreadsManager.stopAll();
    }

    /**
     * Prepare list for send synced items.
     *
     * @param list the list
     * @return the synced items
     */
//todo move to synceditems as a func
    private SyncedItems<?> prepareListForSend(SyncedItems<?> list) {
        SyncedItems<?> ret = new SyncedItems<>(list.syncedListType);
        ret.addAll(list.stream().map(SyncableItem::getSyncableItem).collect(Collectors.toList()));
        return ret.clean();
    }

    /**
     * The entry point of the application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {

        ArgsUtil util = ArgsUtil.create(args);

        START_AT_PORT = util.equalsSign("p").getInt(-1);
        Minimax.SHOW_UI = Minimax.LOG = util.plainTextIgnoreCase(Minimax.DEBUG_MINIMAX).exists();

        Book.checkBook();

        Server server = new Server();
        server.runServer();

        System.out.println("**** ChessServer main() finished! ****");
    }

    /**
     * Send all synced lists.
     *
     * @param player       the player
     * @param excludeLists the exclude lists
     */
    private void sendAllSyncedLists(PlayerNet player, SyncedItems<?>... excludeLists) {
        ArrayList<SyncedItems<?>> lists = new ArrayList<>();
        for (SyncedItems<?> syncedList : syncedLists) {
            if (Arrays.stream(excludeLists).noneMatch(excludedList -> excludedList.equals(syncedList)))
                lists.add(prepareListForSend(syncedList));
        }
        player.getSocketToClient().writeMessage(Message.syncLists(lists.toArray(new SyncedItems[0])));
    }

    /**
     * Run the server - wait for clients to connect and handle them.
     * all processing from now on is done in a handled code. to prevent catastrophic error throwing for any reason.
     */
    public void runServer() {
        ThreadsManager.handleErrors(() -> {
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

    /**
     * Handle client in a separate thread to allow for concurrent client handling.
     *
     * @param playerSocket the socket to the player
     */
    private void handleClient(AppSocket playerSocket) {
        HandledThread.runInHandledThread(() -> {

            MyThread.currentThread(t -> {
                t.addHandler(MyError.DisconnectedError.class, playerSocket::close);
            });
            ServerMessagesHandler messagesHandler = new ServerMessagesHandler(this, playerSocket);
            playerSocket.setMessagesHandler(messagesHandler);
            playerSocket.start();
            appSockets.add(playerSocket);
            PlayerNet player = login(playerSocket);

            messagesHandler.setPlayer(player);
            players.add(player);

            sendAllSyncedLists(player, players);

            log("Client(" + playerSocket.getRemoteAddress() + ") Connected as " + player.getLoginInfo().getUsername());

            gameSetup(player);

        });
    }

    /**
     * login a new client
     *
     * @param appSocket the app socket to the client
     * @return the newly created player net
     * @throws ver14.SharedClasses.Threads.ErrorHandling.MyError.DisconnectedError if the player disconnected while logging in
     */
    public PlayerNet login(AppSocket appSocket) throws MyError.DisconnectedError {
        Message request = appSocket.requestMessage(Message.askForLogin());

        Message responseMessage = responseToLogin(request.getLoginInfo());

        while (responseMessage.getMessageType() == MessageType.ERROR) {
            responseMessage.setRespondingTo(request);
            request = appSocket.requestMessage(responseMessage);

            responseMessage = responseToLogin(request.getLoginInfo());
        }

        appSocket.respond(responseMessage, request);

        LoginInfo loginInfo = request.getLoginInfo();

        if (loginInfo.getLoginType() == LoginType.CANCEL)
            throw new MyError.DisconnectedError();

        if (responseMessage.getMessageType() != MessageType.ERROR) {
            return new PlayerNet(appSocket, loginInfo);
        }
        return login(appSocket);
    }

    /**
     * create a Response to a login message attempt.
     *
     * @param loginInfo the login info
     * @return the response message. a welcome message after a successful login, or an error message.
     * @throws ver14.SharedClasses.Threads.ErrorHandling.MyError.DisconnectedError if the player disconnected while logging in
     */
    private Message responseToLogin(LoginInfo loginInfo) throws MyError.DisconnectedError {
        if (loginInfo == null)
            throw new MyError.DisconnectedError();
        String username = loginInfo.getUsername();
        String password = loginInfo.getPassword();

        return switch (loginInfo.getLoginType()) {
            case NOT_SET_YET -> {
                throw new MyError.DisconnectedError("Received empty login info");
            }
            case LOGIN -> {
                if (DB.isUserExists(username, password)) {
                    if (!isLoggedIn(username)) {
                        loginInfo.setProfilePic(DB.getProfilePic(username));
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

    /**
     * Is logged in boolean.
     *
     * @param username the username
     * @return the boolean
     */
    private boolean isLoggedIn(String username) {
        return players.stream().anyMatch(p -> p.getUsername().equals(username));
    }

    /**
     * End of game session.
     *
     * @param session the session
     */
    public void endOfGameSession(GameSession session) {
        log("game session " + session + " over");
        gameSessions.remove(session.gameID, session);
        (session.getPlayers()).stream().parallel().forEach(player -> {
            if (player.isConnected()) {
                gameSetup(player);
            }
        });
    }

    /**
     * ask the player for his preferred game settings and sets him up for a game (if possible).
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
            playerDisconnected(player, "");
            return;
        }
        if (gameSettings.getGameType() == GameType.QUICK_MATCH) {
            if (gameSettings.isVsAi())
                gameSettings.setGameType(GameType.CREATE_NEW);
            else {
                synchronized (gamePool) {
                    if (!gamePool.values().isEmpty()) {
                        gameSettings.setGameType(GameType.JOIN_EXISTING);
                        gameSettings.setGameID(((GameInfo) gamePool.values().toArray()[0]).gameId);
                    } else {
                        waitingPlayer.set(player);
                        gameSettings.setGameType(GameType.CREATE_NEW);
                    }
                }
            }
        }
        switch (gameSettings.getGameType()) {
            case CREATE_NEW -> {
                if (gameSettings.isVsAi()) {
                    startGameVsAi(player, gameSettings);
                } else {
                    String id = gameIDGenerator.generate();
                    player.setCreatedGameID(id);
                    CreatedGame gameInfo = new CreatedGame(id, player.getUsername(), gameSettings);
                    synchronized (gamePool) {
                        if (waitingPlayer.get() != null && waitingPlayer.get() != player) {
                            startGameSession(gameInfo, player, waitingPlayer.get());
                            waitingPlayer.set(null);
                        } else {
                            gamePool.put(id, gameInfo);
                            player.waitForMatch();
                        }
                    }
                }
            }
            case JOIN_EXISTING -> {
                GameInfo gameInfo;
                gameInfo = removeFromGamePool(gameSettings.getGameID());
                if (gameInfo == null) {
                    player.error("game does not exist");
                } else {
                    startGameSession(gameInfo, getPlayerNet(gameInfo.creatorUsername), player);
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
     * handle a player disconnected event. an attempt will be made to send a bye message to the disconnecting player.<br/>
     * if the player is mid-game, the game session will be notified.<br/>
     * if the player is waiting for a match, he will be removed from the queue.<br/>
     * if the player has a game in the pool (waiting for other players to join to his game) it is removed.<br/>
     *
     * @param player  the player
     * @param message the disconnection description
     */
    public synchronized void playerDisconnected(Player player, String message) {
        if (player == null || players.stream().noneMatch(p -> p.equals(player)))
            return;
        synchronized (waitingPlayer) {
            if (waitingPlayer.get() != null && waitingPlayer.get() == player) {
                waitingPlayer.set(null);
            }
        }
        log(player.getUsername() + " disconnected");
        message = StrUtils.isEmpty(message) ? "bye " + player.getUsername() : message;
        player.disconnect(message, true);

        players.remove(player.getUsername());
        if (player instanceof PlayerNet playerNet)
            appSockets.remove(playerNet.getSocketToClient());
        gamePool.batchRemove(g -> g.creatorUsername.equals(player.getUsername()));

    }

    /**
     * Start game vs ai.
     *
     * @param player       the player
     * @param gameSettings the game settings
     */
    private void startGameVsAi(Player player, GameSettings gameSettings) {
        GameSession gameSession;
        if (gameSettings.getGameType() == GameType.CREATE_NEW) {
            PlayerAI ai = PlayerAI.createPlayerAi(gameSettings.getAISettings());
            gameSession = new GameSession(gameIDGenerator.generate(), player, ai, gameSettings, this);
        } else {
            String resumingId = gameSettings.getGameID();
            UnfinishedGame resumingGame = DB.loadUnfinishedGame(resumingId);
            DB.deleteUnfinishedGame(resumingGame);
            PlayerAI ai = PlayerAI.createPlayerAi(resumingGame.gameSettings.getAISettings());
            gameSession = new GameSession(resumingGame, player, ai, this);
        }
        player.setCreatedGameID(gameSession.gameID);
        gameSessions.add(gameSession);
        gameSession.start();
    }

    /**
     * Start game session between 2 players.
     *
     * @param gameInfo all the settings for the new game
     * @param creator  the game creator
     * @param p2       the 2nd player
     */
    private void startGameSession(GameInfo gameInfo, Player creator, Player p2) {
        GameSession gameSession = new GameSession(gameInfo, creator, p2, this);
        gameSessions.add(gameSession);
        gameSession.start();
    }

    /**
     * Remove from game pool game info.
     *
     * @param gameID the game id
     * @return the game info
     */
    private GameInfo removeFromGamePool(String gameID) {
        return gamePool.remove(gameID);
    }

    /**
     * Gets player net.
     *
     * @param username the username
     * @return the player net
     */
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
     * @param err the error
     */
    @Override
    public void criticalErr(MyError err) {
        closeServer("critical error: " + err);
    }

    /**
     * Db request db response.
     *
     * @param request the request
     * @return the db response
     */
    public DBResponse dbRequest(DBRequest request) {
        DBResponse response = DB.request(request);
        request.getBuilder().getShouldSync().forEach(this::syncedListUpdated);
        return response;
    }

    /**
     * Synced list updated.
     *
     * @param type the type
     */
    private void syncedListUpdated(SyncedListType type) {
        syncedListUpdated(switch (type) {
            case RESUMABLE_GAMES -> {
                throw new IllegalStateException("");
            }
            case CONNECTED_USERS -> players;
            case JOINABLE_GAMES -> gamePool;
            case ONGOING_GAMES -> gameSessions;
        });
    }
}

