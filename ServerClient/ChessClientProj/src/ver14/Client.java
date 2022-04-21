package ver14;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.SharedClasses.DBActions.Arg.ArgType;
import ver14.SharedClasses.DBActions.Arg.Config;
import ver14.SharedClasses.DBActions.Arg.Described;
import ver14.SharedClasses.DBActions.DBRequest.PreMadeRequest;
import ver14.SharedClasses.DBActions.DBResponse.DBResponse;
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
import ver14.view.Dialog.Cards.MessageCard;
import ver14.view.Dialog.Dialogs.ChangePassword.ChangePassword;
import ver14.view.Dialog.Dialogs.GameSelection.GameSelect;
import ver14.view.Dialog.Dialogs.LoginProcess.LoginProcess;
import ver14.view.Dialog.Dialogs.SimpleDialogs.CustomDialog;
import ver14.view.Dialog.Dialogs.SimpleDialogs.InputDialog;
import ver14.view.Dialog.Dialogs.SimpleDialogs.PromotionDialog;
import ver14.view.Dialog.Properties;
import ver14.view.View;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Client דוגמה ללקוח צאט פשוט .
 * ---------------------------------------------------------------------------
 * by Ilan Perez (ilanperets@gmail.com) 20/10/2021
 */
public class Client implements EnvManager {

    // constatns
    private static final int SERVER_DEFAULT_PORT = 1234;
    private static final String teacherIP = "192.168.21.239";
    private static final String teacherAddress = teacherIP + ":" + SERVER_DEFAULT_PORT;


    private static String START_AT_ADDRESS = null;

    public final SoundManager soundManager;
    // for GUI
    private View view;
    // for Client
    private boolean clientSetupOK, clientRunOK;
    private int serverPort;
    private String serverIP;
    private AppSocket clientSocket;
    private PlayerColor myColor;
    private MovesList possibleMoves = null;
    private ViewLocation firstClickLoc;
    private Message lastGetMoveMsg;
    private LoginInfo loginInfo;
    private ClientMessagesHandler msgHandler;
    private boolean isClosing = false;

    private Map<PlayerColor, String> playerUsernames = new HashMap<>();
    private boolean isPremoving = false;
    private boolean isGettingMove = false;

    /**
     * Constractor for Chat Client.
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

        Client client = new Client();
        client.runClient();
    }

    /**
     * Run client.
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

    private void log(String str) {
        System.out.println(str);
    }

    private void setupClientGui() {
        view = new View(this);
    }

    private String showServerAddressDialog() throws UnknownHostException {
        // set the Server Address (DEFAULT IP&PORT)
        serverPort = SERVER_DEFAULT_PORT;
        serverIP = InetAddress.getLocalHost().getHostAddress(); // IP of this computer
        Properties properties = dialogProperties("Server Address");
        String desc = "Enter SERVER Address";
        Described<String> defaultValue = Described.d(serverIP + " : " + serverPort, "Local Host");
        Config<String> config = new Config<>(desc, defaultValue);
        config.addSuggestion(Described.d(teacherAddress, "teacher address"));
        properties.setArgConfig(config);

        InputDialog inputDialog = view.showDialog(new InputDialog(properties, ArgType.ServerAddress));
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
        closeClient(cause, title, MessageCard.MessageType.ERROR);
    }

    /**
     * Close client.
     *
     * @param cause     the cause
     * @param title     the title
     * @param closeType the close type
     */
    public void closeClient(String cause, String title, MessageCard.MessageType closeType) {
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

    void unlockMovableSquares(Message message) {
        this.isGettingMove = true;
        possibleMoves = message.getPossibleMoves();
        unlockPossibleMoves();
    }

    private void unlockPossibleMoves() {
//        view.enableSources(possibleMoves);
        view.enableMyPieces(myColor);
    }


    public void enablePreMove() {
//        isPremoving = true;
//        view.enableSources(PremovesGenerator.generatePreMoves(view.getBoardPnl().createBoard(), getMyColor()));
    }

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

    private String login(String err, Message loginMessage) {

        Properties properties = dialogProperties("Login", "Login", err);
        this.loginInfo = view.showDialog(new LoginProcess(properties)).getLoginInfo();

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
     * Board button pressed.
     *
     * @param clickedLoc the clickedLoc
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
                    assert completeMove != null;
                }
                returnMove(completeMove);
                firstClickLoc = null;
            } else if (clickedLoc.equals(firstClickLoc)) {
                unlockPossibleMoves();
                firstClickLoc = null;
            } else {
                firstClickLoc = clickedLoc;
                view.colorCurrentPiece(firstClickLoc.originalLocation);
                unlockPossibleMoves();
                view.highlightPath(possibleMoves.stream()
                        .filter(move -> move.getMovingFrom().equals(clickedLoc.originalLocation))
                        .toList());
            }
        }
    }

    /**
     * Show promotion dialog piece type.
     *
     * @param clr the clr
     * @return the piece type
     */
    public PieceType showPromotionDialog(PlayerColor clr) {
        return view.showDialog(new PromotionDialog(clr)).getResult().pieceType;
    }

    private void returnMove(Move move) {
        this.isGettingMove = false;
        updateByMove(move);
        clientSocket.writeMessage(Message.returnMove(move, lastGetMoveMsg));
    }

//    private void initGame(Message message) {
//        myColor = message.getPlayerColor();
//        view.initGame(message.getGameTime(), message.getBoard(), myColor, message.getOtherPlayer());
//    }

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
            view.setBtnPiece(move.getMovingFrom(), piece);
        }
        processGameStatus(move.getMoveEvaluation().getGameStatus());

        view.updateByMove(move);

        if (moveEffects) {
            view.colorMove(move);
            soundManager.moved(move, myColor);
        }

    }

    private void processGameStatus(GameStatus gameStatus) {
        if (gameStatus.isCheck()) {
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
            clientSocket.requestMessage(Message.bye(""), response -> {
                view.showMessage(response.getSubject(), "disconnected", MessageCard.MessageType.INFO);
                closeClient();
            });
        } else {
            closeClient();
        }
    }

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
        Question question = new Question("Would you like to play a game?", Question.Answer.YES, Question.Answer.NO);
        CompletableFuture<Question.Answer> futureAns = new CompletableFuture<>();
        view.askQuestion(question, futureAns::complete);
        try {
            Question.Answer answer = futureAns.get();
            if (answer != Question.Answer.YES) {
                return null;
            }
            String msg = "hello %s!\n%s".formatted(loginInfo.getUsername(), StrUtils.createTimeGreeting());
            Properties properties = dialogProperties(msg, "game selection");
            return view.showDialog(new GameSelect(properties)).getGameSettings();
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
    public Properties dialogProperties(String... properties) {
        return new Properties(loginInfo, clientSocket, view.getWin(), new Properties.Details(properties));
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
     * Request.
     *
     * @param builder    the builder
     * @param onResponse the on response
     * @param args       the args
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
        log("handled: " + err.getHandledStr());
    }

    @Override
    public void criticalErr(MyError err) {
        closeClient("critical error", err);
    }

    private void closeClient(String msg, Throwable ex) {
        msg += "\n\n" + MyError.errToString(ex);
        String title = "Runtime Exception: " + msg;

        System.out.println("\n>> " + title);
        System.out.println(">> " + new String(new char[title.length()]).replace('\0', '-'));

        // popup dialog with the error message
        closeClient(msg, "Exception Error", MessageCard.MessageType.ERROR);
    }

    public void delUnf() {
        request(PreMadeRequest.DeleteUnfGames);

    }

    public void request(PreMadeRequest request) {
        request(request, null);
    }

    /**
     * Request.
     *
     * @param request the request
     */
    public void request(PreMadeRequest request, Callback<DBResponse> onResponse) {
        RequestBuilder builder = request.createBuilder();
        Properties properties = dialogProperties(builder.getPreDescription(), builder.getName());
//        Properties properties = new Properties(builder.getPreDescription(), builder.getName());
        CustomDialog customDialog = view.showDialog(new CustomDialog(properties, builder.getArgs()));
        Object[] args = customDialog.getResults();
        request(builder, onResponse, args);
    }

    public void setProfilePic(String profilePic) {
        loginInfo.setProfilePic(profilePic);
        view.authChange(loginInfo);
    }

    public void mapPlayers(PlayerColor myColor, String otherPlayerUn) {
        this.myColor = myColor;
        playerUsernames.put(myColor, getUsername());
        playerUsernames.put(myColor.getOpponent(), otherPlayerUn);
    }

    public void stopPremoving() {

    }

    public void makeRandomMove() {
        if (!isGettingMove)
            return;
        var lst = lastGetMoveMsg.getPossibleMoves();
        returnMove(lst.get(new Random().nextInt(lst.size())));
    }

    public void cancelQuestion(Question.QuestionType questionType) {
        clientSocket.writeMessage(Message.cancelQuestion(new Question("", questionType), getUsername() + " cancelled"));
    }
}
