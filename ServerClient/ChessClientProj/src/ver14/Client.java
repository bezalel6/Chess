package ver14;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.SharedClasses.DBActions.Arg.ArgType;
import ver14.SharedClasses.DBActions.Arg.Config;
import ver14.SharedClasses.DBActions.Arg.Described;
import ver14.SharedClasses.DBActions.DBRequest.PreMadeRequest;
import ver14.SharedClasses.DBActions.DBResponse.TableDBResponse;
import ver14.SharedClasses.DBActions.RequestBuilder;
import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.evaluation.GameStatus;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Game.pieces.Piece;
import ver14.SharedClasses.Game.pieces.PieceType;
import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.LoginType;
import ver14.SharedClasses.Threads.ErrorHandling.EnvManager;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorManager;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.messages.Message;
import ver14.SharedClasses.messages.MessageType;
import ver14.SharedClasses.networking.AppSocket;
import ver14.Sound.SoundManager;
import ver14.view.Board.ViewLocation;
import ver14.view.Dialog.Cards.MessageCard;
import ver14.view.Dialog.Dialogs.ChangePassword.ChangePassword;
import ver14.view.Dialog.Dialogs.DialogProperties.Properties;
import ver14.view.Dialog.Dialogs.GameSelection.GameSelect;
import ver14.view.Dialog.Dialogs.LoginProcess.LoginProcess;
import ver14.view.Dialog.Dialogs.SimpleDialogs.CustomDialog;
import ver14.view.Dialog.Dialogs.SimpleDialogs.InputDialog;
import ver14.view.Dialog.Dialogs.SimpleDialogs.PromotionDialog;
import ver14.view.View;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
    public final SoundManager soundManager;
    // for GUI
    private View view;
    // for Client
    private boolean clientSetupOK, clientRunOK;
    private int serverPort;
    private String serverIP;
    private AppSocket clientSocket;
    private PlayerColor myColor;
    private ArrayList<Move> possibleMoves = null;
    private ViewLocation firstClickLoc;
    private Message lastGetMoveMsg;
    private LoginInfo loginInfo;
    private ClientMessagesHandler msgHandler;

    /**
     * Constractor for Chat Client.
     */
    public Client() {
        ErrorManager.setEnvManager(this);
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

    /**
     * Sets client.
     */
    public void setupClient() {
        try {

            // set the Server Adress (DEFAULT IP&PORT)
            serverPort = SERVER_DEFAULT_PORT;
            serverIP = InetAddress.getLocalHost().getHostAddress(); // IP of this computer
            Properties properties = dialogProperties("Server Address");
            String desc = "Enter SERVER Address";
            Described<String> defaultValue = Described.d(serverIP + " : " + serverPort, "Local Host");
            Config<String> config = new Config<>(desc, defaultValue);
            config.addSuggestion(Described.d(teacherAddress, "teacher address"));
            properties.setArgConfig(config);

            InputDialog inputDialog = view.showDialog(new InputDialog(properties, ArgType.ServerAddress));

            String serverAddress = inputDialog.getInput();

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
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return loginInfo != null ? loginInfo.getUsername() : "not logged in yet";
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
     * Sets my color.
     *
     * @param myColor the my color
     */
    public void setMyColor(PlayerColor myColor) {
        this.myColor = myColor;
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
        if (move.getMoveFlag() == Move.MoveType.Promotion) {
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

    void unlockMovableSquares(Message message) {
        possibleMoves = message.getPossibleMoves();
        unlockPossibleMoves();
    }

    private void unlockPossibleMoves() {
        hideQuestionPnl();
        view.enableSources(possibleMoves);
    }

    /**
     * Hide question pnl.
     */
    void hideQuestionPnl() {
        view.getSidePanel().askPlayerPnl.showPnl(false);
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
            closeClient("canceled", "");
            return null;
        }

        if (response == null) {
            return login("Error reading response from server", loginMessage);
        }
        view.authChange(response.getLoginInfo());
        if (response.getMessageType() == MessageType.ERROR) {
            return login(response.getSubject(), response);
        }
        if (response.getMessageType() == MessageType.WELCOME_MESSAGE) {
            this.loginInfo = response.getLoginInfo();
        }
        return this.loginInfo.getUsername();
    }

    /**
     * Board button pressed.
     *
     * @param loc the loc
     */
    public void boardButtonPressed(ViewLocation loc) {
        if (possibleMoves != null) {
            view.resetBackground();
            view.enableAllSquares(false);
            Move completeMove = getMoveFromDest(loc);
            if (completeMove != null) {
                if (completeMove.getMoveFlag() == Move.MoveType.Promotion) {
                    completeMove.setPromotingTo(showPromotionDialog(myColor));
                }
                returnMove(completeMove);
                firstClickLoc = null;
            } else if (loc.equals(firstClickLoc)) {
                unlockPossibleMoves();
                firstClickLoc = null;
            } else {
                firstClickLoc = loc;
                view.colorCurrentPiece(firstClickLoc.originalLocation);
                unlockPossibleMoves();
                view.highlightPath(possibleMoves.stream()
                        .filter(move -> move.getMovingFrom().equals(loc.originalLocation))
                        .collect(Collectors.toCollection(ArrayList::new)));
            }
        }
    }

//    private void initGame(Message message) {
//        myColor = message.getPlayerColor();
//        view.initGame(message.getGameTime(), message.getBoard(), myColor, message.getOtherPlayer());
//    }

    private Move getMoveFromDest(ViewLocation clickedOn) {
        if (firstClickLoc == null)
            return null;
        return possibleMoves.stream()
                .filter(move ->
                        move.getMovingFrom().equals(firstClickLoc.originalLocation) &&
                                move.getMovingTo().equals(clickedOn.originalLocation))
                .findAny().orElse(null);
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
        clientSocket.writeMessage(Message.returnMove(move, lastGetMoveMsg));
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
        clientSocket.writeMessage(new Message(MessageType.OFFER_DRAW));
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
        String msg = "hello %s!\n%s".formatted(loginInfo.getUsername(), StrUtils.createTimeGreeting());
        Properties properties = dialogProperties(msg, "game selection");
        return view.showDialog(new GameSelect(properties)).getGameSettings();
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
            request(RequestBuilder.changePassword(), msg -> {
                if (msg != null && msg.getDBResponse().getStatus() == TableDBResponse.Status.SUCCESS) {
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
    public void request(RequestBuilder builder, MessageCallback onResponse, Object... args) {
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
            if (onResponse != null)
                onResponse.onMsg(msg);
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
        String title = "Runtime Exception: " + msg;

        System.out.println("\n>> " + title);
        System.out.println(">> " + new String(new char[title.length()]).replace('\0', '-'));

        // popup dialog with the error message
        closeClient(msg + "\n\n" + MyError.errToString(ex), "Exception Error", MessageCard.MessageType.ERROR);
    }

    public void delUnf() {
        request(PreMadeRequest.deleteUnfGames);

    }

    /**
     * Request.
     *
     * @param request the request
     */
    public void request(PreMadeRequest request) {
        RequestBuilder builder = request.createBuilder();
        Properties properties = dialogProperties(builder.getPreDescription(), builder.getName());
//        Properties properties = new Properties(builder.getPreDescription(), builder.getName());
        CustomDialog customDialog = view.showDialog(new CustomDialog(properties, builder.getArgs()));
        Object[] args = customDialog.getResults();
        request(builder, null, args);
    }
}
