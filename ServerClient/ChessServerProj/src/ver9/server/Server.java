package ver9.server;

import ver9.SharedClasses.*;
import ver9.SharedClasses.SavedGames.CreatedGame;
import ver9.SharedClasses.SavedGames.GameInfo;
import ver9.SharedClasses.SavedGames.UnfinishedGame;
import ver9.SharedClasses.Sync.SyncableItem;
import ver9.SharedClasses.Sync.SyncedItems;
import ver9.SharedClasses.Sync.SyncedListType;
import ver9.SharedClasses.Sync.UserInfo;
import ver9.SharedClasses.messages.Message;
import ver9.SharedClasses.messages.MessageType;
import ver9.SharedClasses.networking.AppSocket;
import ver9.SharedClasses.networking.MyErrors;
import ver9.SharedClasses.ui.windows.CloseConfirmationJFrame;
import ver9.server.DB.DB;
import ver9.server.game.Game;
import ver9.server.game.GameSession;
import ver9.server.players.Player;
import ver9.server.players.PlayerAI;
import ver9.server.players.PlayerNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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
    private final static IDsGenerator gameIDGenerator;

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

    private final SyncedItems netPlayers;
    private final SyncedItems gameSessions;
    private final SyncedItems gamePool;
    private final ArrayList<SyncedItems> syncedLists;
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
        netPlayers = new SyncedItems(SyncedListType.CONNECTED_USERS, this::syncedListUpdated);
        gamePool = new SyncedItems(SyncedListType.JOINABLE_GAMES, this::syncedListUpdated);
        gameSessions = new SyncedItems(SyncedListType.ONGOING_GAMES, this::syncedListUpdated);

        this.syncedLists = new ArrayList<>() {{
            add(netPlayers);
            add(gamePool);
            add(gameSessions);
        }};
    }

    // create server GUI
    private void createServerGUI() {
        frmWin = new CloseConfirmationJFrame(this::exitServer) {{
            setSize(SERVER_WIN_SIZE);
            setAlwaysOnTop(true);
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

    public void syncedListUpdated(SyncedItems list) {
        list = prepareListForSend(list);
        SyncedItems finalList = list;
        netPlayers.forEachItem(playerNet -> ((PlayerNet) playerNet).getSocketToClient().writeMessage(Message.syncLists(finalList)));
    }

    private void exitServer() {
        closeServer("");
    }

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

    private SyncedItems prepareListForSend(SyncedItems list) {
        if (list.syncedListType == SyncedListType.CONNECTED_USERS) {
            SyncedItems userInfos = new SyncedItems(list.syncedListType);
            list.forEachItem(player -> userInfos.add(new UserInfo(((PlayerNet) player).ID())));
            list = userInfos;
        } else if (list.syncedListType == SyncedListType.ONGOING_GAMES) {
            SyncedItems gameInfos = new SyncedItems(list.syncedListType);
            list.forEachItem(gameSession -> {
                Game game = ((GameSession) gameSession).getGame();
                gameInfos.add(game.getCurrentGameState());
            });
            list = gameInfos;
        }
        return list.clean();
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
            netPlayers.forEachItem(netPlayer -> ((PlayerNet) netPlayer).disconnect("Server Closing"));

            log("Server Stopped!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void log(String msg) {
        msg = StrUtils.format(msg);
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

    private void sendAllLists(PlayerNet player, SyncedItems... excludeLists) {
        ArrayList<SyncedItems> lists = new ArrayList<>();
        for (SyncedItems syncedList : syncedLists) {
            if (Arrays.stream(excludeLists).noneMatch(list -> list.equals(syncedList)))
                lists.add(prepareListForSend(syncedList));
        }
        player.getSocketToClient().writeMessage(Message.syncLists(lists.toArray(new SyncedItems[0])));
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
                else {
                    handleClient(appSocketToPlayer);
                }
            }
        }

        closeServer("runServer() finised!");

        System.out.println("**** runServer() finished! ****");
    }

    // handle client in a separate thread
    private void handleClient(AppSocket playerSocket) {
        new Thread(() -> {
            ClientMessagesHandler clientMessagesHandler = new ClientMessagesHandler(this, playerSocket);
            playerSocket.setMessagesHandler(clientMessagesHandler);
//            serverSocket.addClientSocket(playerSocket);
            playerSocket.startReading();
            PlayerNet player = login(playerSocket);
            if (player == null) {
                playerSocket.stopReading();
                return;
            }

            clientMessagesHandler.setPlayer(player);
            netPlayers.add(player);

            sendAllLists(player, netPlayers);

            log("Client(" + playerSocket.getRemoteAddress() + ") Connected as " + player.getLoginInfo().getUsername());

            gameSetup(player);

        }).start();
    }

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
            return new PlayerNet(appSocket, loginInfo, loginInfo.getUsername());
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
        return netPlayers.stream().anyMatch(p -> ((PlayerNet) p).getUsername().equals(username));
    }

    public void disconnectPlayer(Player player, MyErrors err) {
        disconnectPlayer(player, "error occurred. " + err);
    }

    public void disconnectPlayer(Player player, String cause) {
        if (player != null) {
            log("disconnected " + player.getUsername() + " " + cause);
            player.disconnect(cause);
            Game ongoingGame = player.getOnGoingGame();
            if (ongoingGame != null) {
                ongoingGame.playerDisconnected(player);
            }
            if (player instanceof PlayerNet) {
                netPlayers.remove(((PlayerNet) player).ID());
            }
            List<SyncableItem> del = gamePool.values()
                    .stream()
                    .filter(game -> ((CreatedGame) game).creatorUsername.equals(player.getUsername()))
                    .toList();
            del.forEach(deleting -> gamePool.remove(((CreatedGame) deleting).gameId));
        }
    }

    public void endOfGameSession(GameSession session) {
        gameSessions.remove(session.gameID);
        Arrays.stream(session.getPlayers()).parallel().forEach(player -> {
            if (player.isConnected()) {
                gameSetup(player);
            }
        });
    }

    public void gameSetup(Player player) {
        SyncedItems joinable = getJoinableGames(player);
        SyncedItems resumable = getResumableGames(player);

        GameSettings gameSettings = player.getGameSettings(joinable, resumable);
        if (gameSettings == null) {
            disconnectPlayer(player);
            return;
        }
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
                GameInfo gameInfo = (GameInfo) gamePool.get(gameSettings.getGameID());
                if (gameInfo == null) {
                    throw new Error("user should get an error");
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

    public SyncedItems getJoinableGames(Player playerNet) {
        return gamePool.clean();
    }

    public SyncedItems getResumableGames(Player playerNet) {
        return DB.getUnfinishedGames(playerNet.getUsername()).clean();
    }

    public void disconnectPlayer(Player player) {
        disconnectPlayer(player, "");
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
        return (PlayerNet) netPlayers.stream()
                .filter(p -> ((PlayerNet) p).getUsername().equals(username))
                .findAny()
                .orElse(null);
    }

    public ArrayList<String> createUsernameSuggestions(String username) {
        int maxSuggestions = 5;
        int numOfIterations = 10;
        ArrayList<String> suggestions = new ArrayList<>();
        ArrayList<ArrayList<String>> options = new ArrayList<>();
        Pattern pattern = Pattern.compile("([0-9]+)");
        pattern.matcher(username).results().forEach(match -> {
            int num = Integer.parseInt(match.group());
            ArrayList<String> strs = new ArrayList<>();
            for (int i = 0; i < numOfIterations; i++) {
                num++;
                String str = username.substring(0, match.start()).concat(num + "").concat(username.substring(match.end()));
                strs.add(str);
            }
            options.add(strs);
        });
        outer:
        for (ArrayList<String> list : options) {
            for (String option : list) {
                if (!DB.isUsernameExists(option)) {
                    suggestions.add(option);
                    if (suggestions.size() >= maxSuggestions)
                        break outer;
                    //todo try to switch to a different list to make suggestions more unique
                }
            }
        }
        return suggestions;
    }
}
