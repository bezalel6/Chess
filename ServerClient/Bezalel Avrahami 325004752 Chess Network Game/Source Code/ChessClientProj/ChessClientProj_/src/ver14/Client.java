package ver14;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.SharedClasses.DBActions.Arg.ArgType;
import ver14.SharedClasses.DBActions.Arg.Config;
import ver14.SharedClasses.DBActions.Arg.Described;
import ver14.SharedClasses.DBActions.DBRequest.PreMadeRequest;
import ver14.SharedClasses.DBActions.RequestBuilder;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.BasicMove;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.Moves.MovesList;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.Login.LoginType;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.Networking.AppSocket;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.SharedClasses.Networking.Messages.MessageType;
import ver14.SharedClasses.Threads.ErrorHandling.EnvManager;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Threads.MyThread;
import ver14.SharedClasses.Utils.ArgsUtil;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.Sound.SoundManager;
import ver14.view.Board.ViewLocation;
import ver14.view.Dialog.DialogDetails;
import ver14.view.Dialog.DialogProperties;
import ver14.view.Dialog.Dialogs.ChangePassword.ChangePassword;
import ver14.view.Dialog.Dialogs.GameSelection.GameSelect;
import ver14.view.Dialog.Dialogs.LoginProcess.LoginProcess;
import ver14.view.Dialog.Dialogs.OtherDialogs.CustomDialog;
import ver14.view.Dialog.Dialogs.OtherDialogs.InputDialog;
import ver14.view.Dialog.Dialogs.Promotion.Promotion;
import ver14.view.View;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import ver14.SharedClasses.DBActions.DBResponse.DBResponse;

/**
 * represents a chess Client that sets up the connection between the client and server and manages the {@link View}, {@link ver14.SharedClasses.DBActions.DBRequest.DBRequest}s, move selection, and a major part of the communication
 * with the server through the {@link AppSocket}.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Client implements EnvManager {

    /**
     * The constant SERVER_DEFAULT_PORT.
     */
// constatns
    private static final int SERVER_DEFAULT_PORT = 1234;
    /**
     * The constant teacherIP.
     */
    private static final String teacherIP = "192.168.21.239";
    /**
     * The constant teacherAddress.
     */
    private static final String teacherAddress = teacherIP + ":" + SERVER_DEFAULT_PORT;


    /**
     * The constant START_AT_ADDRESS.
     */
    private static String START_AT_ADDRESS = null;
    /**
     * The constant START_FULLSCREEN.
     */
    private static boolean START_FULLSCREEN = false;

    /**
     * The Sound manager.
     */
    public final SoundManager soundManager;
    /**
     * The View.
     */
// for GUI
    private View view;
    /**
     * The Client setup ok.
     */
// for Client
    private boolean clientSetupOK, /**
     * The Client run ok.
     */
    clientRunOK;
    /**
     * The Server port.
     */
    private int serverPort;
    /**
     * The Server ip.
     */
    private String serverIP;
    /**
     * The Client socket.
     */
    private AppSocket clientSocket;
    /**
     * The My color.
     */
    private PlayerColor myColor;
    /**
     * The Possible moves.
     */
    private MovesList possibleMoves = null;
    /**
     * The First click loc.
     */
    private ViewLocation firstClickLoc;
    /**
     * The Last get move msg.
     */
    private Message lastGetMoveMsg;
    /**
     * The Login info.
     */
    private LoginInfo loginInfo;
    /**
     * The Msg handler.
     */
    private ClientMessagesHandler msgHandler;
    /**
     * The Is closing.
     */
    private boolean isClosing = false;

    /**
     * The Player usernames.
     */
    private Map<PlayerColor, String> playerUsernames = new HashMap<>();
    /**
     * The Is premoving.
     */
    private boolean isPremoving = false;
    /**
     * The Is getting move.
     */
    private boolean isGettingMove = false;

    /**
     * Constructor for Chess Client.
     */
    public Client() {
        MyThread.setEnvManager(this);
        soundManager = new SoundManager();
        setupClientGui();
        setupClient();
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
// main
    public static void main(String[] args) {
        
        ArgsUtil util = ArgsUtil.create(args);
        START_AT_ADDRESS = util.equalsSign("address").getString();
        START_FULLSCREEN = util.plainTextIgnoreCase("-f").exists();
        Client client = new Client();
        client.runClient();
    }

    /**
     * start listening to messages from the server.
     */
    public void runClient() {
        if (clientSetupOK) {
            log("Connected to Server(" + clientSocket.getRemoteAddress() + ")");
            log("CLIENT(" + clientSocket.getLocalAddress() + ") Setup & Running!\n");
            firstClickLoc = null;
            msgHandler = new ClientMessagesHandler(this, view);
            clientSocket.setMessagesHandler(msgHandler);
            clientSocket.start();
            view.connectedToServer();
        }
    }

    /**
     * Log.
     *
     * @param str the str
     */
    private void log(String str) {
        System.out.println(str);
    }

    /**
     * Sets client gui.
     */
    private void setupClientGui() {
        view = new View(this);
        if (START_FULLSCREEN)
            view.getWin().toggleFullscreen();
    }

    /**
     * Show server address dialog string.
     *
     * @return the string
     * @throws UnknownHostException the unknown host exception
     */
    private String showServerAddressDialog() throws UnknownHostException {
        // set the Server Address (DEFAULT IP&PORT)
        serverPort = SERVER_DEFAULT_PORT;
        serverIP = InetAddress.getLocalHost().getHostAddress(); // IP of this computer
        DialogProperties dialogProperties = dialogProperties("Server Address");
        String desc = "Enter SERVER Address";
        Described<String> defaultValue = new Described<>(serverIP + " : " + serverPort, "Local Host");
        Config<String> config = new Config<>(desc, defaultValue);
        config.addSuggestion(new Described<>(teacherAddress, "teacher address"));
        dialogProperties.setArgConfig(config);

        InputDialog inputDialog = view.showDialog(new InputDialog(dialogProperties, ArgType.ServerAddress));
        return inputDialog.getInput();
    }

    /**
     * Sets client.
     */
    public void setupClient() {
        try {

            String serverAddress;
            if (StrUtils.isEmpty(START_AT_ADDRESS)) {
                serverAddress = showServerAddressDialog();
            } else {
                serverAddress = START_AT_ADDRESS;
            }

            // check if Cancel button was pressed
            if (serverAddress == null) {
                closeClient();
                return;
            }

            log("trying to connect to: " + serverAddress);

            // get server IP & PORT from input string
            serverAddress = serverAddress.replace(" ", ""); // remove all spaces
            serverIP = serverAddress.substring(0, serverAddress.indexOf(":"));
            serverPort = Integer.parseInt(serverAddress.substring(serverAddress.indexOf(":") + 1));

            // Setup connection to SERVER Address
            clientSocket = new AppSocket(serverIP, serverPort);

            clientSetupOK = true;
        } catch (Exception exp) {
            clientSetupOK = false;
            String serverAddress = serverIP + ":" + serverPort;
            closeClient("Client can't connect to Server(" + serverAddress + ")", exp);
        }
    }

    /**
     * Disconnected from server.
     */
    public void disconnectedFromServer() {
        closeClient("The connection with the Server is LOST!\nClient will close now...", "Chess Client Error");
    }

    /**
     * Close client.
     *
     * @param cause the cause
     * @param title the title
     */
    public void closeClient(String cause, String title) {
        closeClient(cause, title, ver14.view.Dialog.Cards.MessageCard.MessageType.ERROR);
    }

    /**
     * Close client.
     *
     * @param cause     the cause
     * @param title     the title
     * @param closeType the close type
     */
    public void closeClient(String cause, String title, ver14.view.Dialog.Cards.MessageCard.MessageType closeType) {
        if (isClosing)
            return;
        isClosing = true;
        if (clientSocket != null) {
            clientSocket.close(); // will throw 'SocketException' and unblock I/O. see close() API

        }

        if (cause != null) {
            title = title == null ? "Closing" : title;
            view.showMessage(cause, title, closeType);
        }

        log("Client Closed!");

        // close GUI
        closeGui();
        // close client
        System.exit(0);
    }

    /**
     * Close gui.
     */
    public void closeGui() {
        clientRunOK = false;
        view.dispose();
    }

    /**
     * Gets messages handler.
     *
     * @return the messages handler
     */
    public ClientMessagesHandler getMessagesHandler() {
        return msgHandler;
    }

    /**
     * Gets player usernames.
     *
     * @return the player usernames
     */
    public Map<PlayerColor, String> getPlayerUsernames() {
        return playerUsernames;
    }

    /**
     * Gets client socket.
     *
     * @return the client socket
     */
    public AppSocket getClientSocket() {
        return clientSocket;
    }

    /**
     * Update game time.
     *
     * @param message the message
     */
    public void updateGameTime(Message message) {
        view.setGameTime(message.getGameTime());
    }

    /**
     * Unlock movable squares.
     *
     * @param message the message
     */
    void unlockMovableSquares(Message message) {
        this.isGettingMove = true;
        possibleMoves = message.getPossibleMoves();
        unlockPossibleMoves();
    }

    /**
     * Unlock possible moves.
     */
    private void unlockPossibleMoves() {
//        view.enableSources(possibleMoves);
        view.enablePieces(myColor);
    }


    /**
     * Enable pre move.
     */
    public void enablePreMove() {
//        isPremoving = true;
//        view.enableSources(PremovesGenerator.generatePreMoves(view.getBoardPnl().createBoard(), getMyColor()));
    }

    /**
     * Gets my color.
     *
     * @return the my color
     */
    public PlayerColor getMyColor() {
        return myColor;
    }

    /**
     * Login string.
     *
     * @param loginMessage the login message
     * @return the string
     */
    String login(Message loginMessage) {
        return login("", loginMessage);
    }

    /**
     * Login.
     *
     * @param err          the error from an earlier login attempt
     * @param loginMessage the login request message from the server
     * @return null if user cancelled the login, otherwise the client's username.
     */
    private String login(String err, Message loginMessage) {

        DialogProperties dialogProperties = dialogProperties("Login", "Login", err);
        this.loginInfo = view.showDialog(new LoginProcess(dialogProperties)).getLoginInfo();

        Message response = clientSocket.requestMessage(Message.returnLogin(loginInfo, loginMessage));

        if (this.loginInfo.getLoginType() == LoginType.CANCEL) {
            closeClient();
            return null;
        }

        if (response == null) {
            return login("Error reading response from server", loginMessage);
        }
        if (response.getMessageType() == MessageType.ERROR) {
            return login(response.getSubject(), response);
        }
        if (response.getMessageType() == MessageType.WELCOME_MESSAGE) {
            this.loginInfo = response.getLoginInfo();
            view.authChange(response.getLoginInfo());
        }
        return this.loginInfo.getUsername();
    }

    /**
     * handle board button pressed.
     *
     * @param clickedLoc the button's location
     */
    public void boardButtonPressed(ViewLocation clickedLoc) {
        if (possibleMoves != null) {
            view.resetBackground();
            view.enableAllSquares(false);
            BasicMove basicMove = null;
            Move completeMove = null;
            if (firstClickLoc != null) {
                basicMove = new BasicMove(firstClickLoc.originalLocation, clickedLoc.originalLocation);
                completeMove = possibleMoves.findMove(basicMove);
            }
            if (completeMove != null) {
                if (completeMove.getMoveFlag() == Move.MoveFlag.Promotion) {
                    PieceType promotingTo = showPromotionDialog(myColor);
                    completeMove = possibleMoves.findMove(basicMove, m -> m.getPromotingTo() == promotingTo);
                }
                returnMove(completeMove);
                firstClickLoc = null;
            } else if (clickedLoc.equals(firstClickLoc)) {
                unlockPossibleMoves();
                firstClickLoc = null;
            } else {
                firstClickLoc = clickedLoc;
                view.setCurrentPiece(firstClickLoc.originalLocation);
                unlockPossibleMoves();
                view.highlightPath(possibleMoves.stream()
                        .filter(move -> move.getSource().equals(clickedLoc.originalLocation))
                        .toList());
            }
        }
    }

    /**
     * Show promotion dialog and return the selected piece type.
     *
     * @param clr the color pieces the player can choose from (white pieces for the white player...)
     * @return the chosen piece type
     */
    public PieceType showPromotionDialog(PlayerColor clr) {
        return view.showDialog(new Promotion(clr)).getResult().pieceType;
    }

    /**
     * Return move.
     *
     * @param move the move
     */
    private void returnMove(Move move) {
        this.isGettingMove = false;
        updateByMove(move);
        clientSocket.writeMessage(Message.returnMove(move, lastGetMoveMsg));
        this.possibleMoves = null;
    }

    /**
     * Update by move.
     *
     * @param move the move
     */
    public void updateByMove(Move move) {
        updateByMove(move, true);
    }

    /**
     * Update by move.
     *
     * @param move        the move
     * @param moveEffects the move effects: sound and color
     */
    public void updateByMove(Move move, boolean moveEffects) {
        view.resetBackground();
        if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
            Piece piece = Piece.getPiece(move.getPromotingTo(), move.getMovingColor());
            view.setBtnPiece(move.getSource(), piece);
        }
        processGameStatus(move.getMoveEvaluation().getGameStatus());

        view.updateByMove(move);

        if (moveEffects) {
            view.colorMove(move);
            soundManager.moved(move, myColor);
        }

    }

    /**
     * Process game status.
     *
     * @param gameStatus the game status
     */
    private void processGameStatus(GameStatus gameStatus) {
        if (gameStatus.isCheckOrMate()) {
            view.inCheck(gameStatus.getCheckedKingLoc());
        }

    }

    /**
     * Resign btn clicked.
     */
    public void resignBtnClicked() {
        clientSocket.writeMessage(new Message(MessageType.RESIGN));
    }

    /**
     * Offer draw btn clicked.
     */
    public void offerDrawBtnClicked() {
        clientSocket.writeMessage(Message.askQuestion(Question.drawOffer(getUsername())));
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return loginInfo != null ? loginInfo.getUsername() : "not logged in yet";
    }

    /**
     * Gets view.
     *
     * @return the view
     */
    public View getView() {
        return view;
    }

    /**
     * Sets latest get move msg.
     *
     * @param lastGetMoveMsg the last get move msg
     */
    public void setLatestGetMoveMsg(Message lastGetMoveMsg) {
        this.lastGetMoveMsg = lastGetMoveMsg;
    }

    /**
     * Disconnect from server.
     */
    public void disconnectFromServer() {
        if (clientSocket != null && clientSocket.isConnected()) {
            clientSocket.writeMessage(Message.bye(""));
//            clientSocket.requestMessage(, response -> {
//                view.showMessage(response.getSubject(), "disconnected", MessageCard.MessageType.INFO);
//                closeClient();
//            });
        } else {
            closeClient();
        }
    }

    /**
     * Close client.
     */
    private void closeClient() {
        closeClient(null, null, null);
    }

    /**
     * Stop running time.
     */
    public void stopRunningTime() {
        view.getSidePanel().stopRunningTime();
    }

    /**
     * Start my time.
     */
    public void startMyTime() {
        view.getSidePanel().startRunning(myColor);
    }

    /**
     * Start opponent time.
     */
    public void startOpponentTime() {
        view.getSidePanel().startRunning(myColor.getOpponent());
    }

    /**
     * Show game settings dialog game settings.
     *
     * @return the game settings
     */
    public GameSettings showGameSettingsDialog() {
        Question.Answer quickMatchVsAi = new Question.Answer("Play Game vs AI");
        Question.Answer quickMatchVsReal = new Question.Answer("Play Game");

        Question.Answer matchSettings = new Question.Answer("Settings");
        Question match = new Question("Play Game", quickMatchVsReal, quickMatchVsAi, matchSettings);

        CompletableFuture<Question.Answer> futureAns = new CompletableFuture<>();
        view.askQuestion(match, futureAns::complete);

        try {
            Question.Answer answer = futureAns.get();
            if (answer.equals(quickMatchVsAi)) {
                GameSettings settings = new GameSettings();
                settings.initDefault1vAi();
                return settings;
            }
            if (answer.equals(quickMatchVsReal)) {
                GameSettings settings = new GameSettings();
                settings.initDefault1v1();
                settings.setGameType(ver14.SharedClasses.Game.GameSetup.GameType.QUICK_MATCH);
                return settings;

            }
            String msg = "hello %s!\n%s".formatted(loginInfo.getUsername(), StrUtils.createTimeGreeting());
            DialogProperties dialogProperties = dialogProperties(msg, "game selection");
            return view.showDialog(new GameSelect(dialogProperties)).getGameSettings();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates Dialog Properties
     *
     * @param properties []/[header]/[header,title]/[header,title,error]
     * @return the created properties
     */
    public DialogProperties dialogProperties(String... properties) {
        return new DialogProperties(loginInfo, clientSocket, view.getWin(), new DialogDetails(properties));
    }

    /**
     * Change password.
     */
    public void changePassword() {
        ChangePassword changePassword = view.showDialog(new ChangePassword(dialogProperties("change password"), loginInfo.getPassword()));
        String pw = changePassword.getPassword();
        if (pw != null) {
            request(RequestBuilder.changePassword(), res -> {
                if (res != null && res.isSuccess()) {
                    loginInfo.setPassword(pw);
                }
            }, null, pw);
        }

    }

    /**
     * send a db request to the server.
     * {@link RequestBuilder}s use arguments to create dynamic requests. some arguments are expected to be filled by
     * the player, and will show a dialog for getting that info, and some are reliant on the environment to provide the value.
     *
     * @param builder    the request builder
     * @param onResponse a callback to call with a db response
     * @param args       the arguments for the request
     */
    public void request(RequestBuilder builder, Callback<DBResponse> onResponse, Object... args) {
        if (args == null) {
            return;
        }

        for (int i = 0; i < args.length; i++) {
            Arg arg = builder.getArgs()[i];
            Object argVal = args[i];
            if (!arg.isUserInput()) {
                if (arg.argType == ArgType.Username) {
                    argVal = loginInfo.getUsername();
                }
            }
            if (argVal == null) {//user clicked cancel in the input dialog
                return;
            }
            args[i] = argVal;
        }
        clientSocket.requestMessage(Message.dbRequest(builder.build(args)), msg -> {
            if (msg == null)
                return;
            if (onResponse != null)
                onResponse.callback(msg.getDBResponse());
            view.showDBResponse(msg.getDBResponse(), builder.getName(), builder.getName());
        });
    }

    @Override
    public void handledErr(MyError err) {
        log("handled: " + err.getShortDesc());
    }

    @Override
    public void criticalErr(MyError err) {
        closeClient("critical error", err);
    }

    /**
     * Close client.
     *
     * @param msg the msg
     * @param ex  the ex
     */
    private void closeClient(String msg, Throwable ex) {
        msg += "\n\n" + MyError.errToString(ex);
        String title = "Runtime Exception: " + msg;

        System.out.println("\n>> " + title);
        System.out.println(">> " + new String(new char[title.length()]).replace('\0', '-'));

        // popup dialog with the error message
        closeClient(msg, "Exception Error", ver14.view.Dialog.Cards.MessageCard.MessageType.ERROR);
    }

    /**
     * Delete unfinished games.
     */
    public void delUnf() {
        request(PreMadeRequest.DeleteUnfGames);

    }

    /**
     * Request.
     *
     * @param request the request
     */
    public void request(PreMadeRequest request) {
        request(request, null);
    }

    /**
     * Request.
     *
     * @param request    the request
     * @param onResponse the on response
     */
    public void request(PreMadeRequest request, Callback<DBResponse> onResponse) {
        RequestBuilder builder = request.createBuilder();
        DialogProperties dialogProperties = dialogProperties(builder.getPreDescription(), builder.getName());
//        Properties properties = new Properties(builder.getPreDescription(), builder.getName());
        CustomDialog customDialog = view.showDialog(new CustomDialog(dialogProperties, builder.getArgs()));
        Object[] args = customDialog.getResults();
        request(builder, onResponse, args);
    }

    /**
     * Sets profile pic.
     *
     * @param profilePic the profile pic
     */
    public void setProfilePic(String profilePic) {
        loginInfo.setProfilePic(profilePic);
        view.authChange(loginInfo);
    }

    /**
     * Map players.
     *
     * @param myColor       the my color
     * @param otherPlayerUn the other player un
     */
    public void mapPlayers(PlayerColor myColor, String otherPlayerUn) {
        this.myColor = myColor;
        playerUsernames.put(myColor, getUsername());
        playerUsernames.put(myColor.getOpponent(), otherPlayerUn);
    }

    /**
     * Stop premoving.
     */
    public void stopPremoving() {

    }

    /**
     * Make random move.
     */
    public void makeRandomMove() {
        if (!isGettingMove)
            return;
        var lst = lastGetMoveMsg.getPossibleMoves();
        returnMove(lst.get(new Random().nextInt(lst.size())));
    }

    /**
     * Cancel question.
     *
     * @param questionType the question type
     */
    public void cancelQuestion(Question.QuestionType questionType) {
        clientSocket.writeMessage(Message.cancelQuestion(new Question("", questionType), getUsername() + " cancelled"));
    }

    /**
     * Is my turn boolean.
     *
     * @return the boolean
     */
    public boolean isMyTurn() {
        return isGettingMove;
    }
}
