package ver14;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.SharedClasses.DBActions.Arg.ArgType;
import ver14.SharedClasses.DBActions.Arg.Config;
import ver14.SharedClasses.DBActions.DBRequest.PreMadeRequest;
import ver14.SharedClasses.DBActions.DBResponse;
import ver14.SharedClasses.DBActions.RequestBuilder;
import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.evaluation.GameStatus;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Game.pieces.Piece;
import ver14.SharedClasses.Game.pieces.PieceType;
import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.LoginType;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.messages.Message;
import ver14.SharedClasses.messages.MessageType;
import ver14.SharedClasses.networking.AppSocket;
import ver14.SharedClasses.ui.dialogs.MyDialogs;
import ver14.SharedClasses.ui.dialogs.MyDialogs.DialogOption;
import ver14.view.Board.ViewLocation;
import ver14.view.Dialog.Cards.MessageCard;
import ver14.view.Dialog.Dialogs.ChangePassword.ChangePassword;
import ver14.view.Dialog.Dialogs.DialogProperties.Properties;
import ver14.view.Dialog.Dialogs.GameSelection.GameSelect;
import ver14.view.Dialog.Dialogs.LoginProcess.LoginProcess;
import ver14.view.Dialog.Dialogs.SimpleDialogs.CustomDialog;
import ver14.view.Dialog.Dialogs.SimpleDialogs.InputDialog;
import ver14.view.IconManager.IconManager;
import ver14.view.View;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Client דוגמה ללקוח צאט פשוט .
 * ---------------------------------------------------------------------------
 * by Ilan Perez (ilanperets@gmail.com) 20/10/2021
 */
public class Client {

    // constatns
    private static final int SERVER_DEFAULT_PORT = 1234;
    private static final String teacherIP = "192.168.21.239";
    private static final String teacherAddress = teacherIP + ":" + SERVER_DEFAULT_PORT;
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
        setupClientGui();
        setupClient();
    }

    private void setupClientGui() {
        view = new View(this);
    }

    public void setupClient() {
        try {
            // set the Server Adress (DEFAULT IP&PORT)
            serverPort = SERVER_DEFAULT_PORT;
            serverIP = InetAddress.getLocalHost().getHostAddress(); // IP of this computer

            // get Server Address from user
//            String serverAddress = JOptionPane.showInputDialog(view.getWin(), "Enter SERVER Address [IP : PORT] or \"teacher\"", serverIP + " : " + serverPort);

            Properties properties = dialogProperties("Server Address");
            Config<String> config = new Config<>("Enter SERVER Address [IP : PORT] or \"teacher\"", serverIP + " : " + serverPort, "Local Host");
            properties.setArgConfig(config);
            InputDialog inputDialog = view.showDialog(new InputDialog(properties));

            String serverAddress = inputDialog.getInput();

            if (serverAddress != null && serverAddress.equalsIgnoreCase("teacher")) {
                serverAddress = teacherAddress;
            }
            // check if Cancel button was pressed
            if (serverAddress == null) {
                disconnectedFromServer();
                return;
            }

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
            log("Client can't connect to Server(" + serverAddress + ")", exp);
        }
    }

    /**
     * Creates Dialog Properties
     *
     * @param properties []/[header]/[header,title]/[header,title,error]
     * @return the created properties
     */
    public Properties dialogProperties(String... properties) {
        return new Properties(clientSocket, view.getWin(), new Properties.Details(properties));
    }

    public void disconnectedFromServer() {
        closeClient("The connection with the Server is LOST!\nClient will close now...", "Chat Client Error");
    }

    private void log(String msg, Exception ex) {
        String title = "Runtime Exception: " + msg;

        System.out.println("\n>> " + title);
        System.out.println(">> " + new String(new char[title.length()]).replace('\0', '-'));

        String errMsg = ">> " + ex.toString() + "\n";
        for (StackTraceElement element : ex.getStackTrace())
            errMsg += ">>> " + element + "\n";
        System.out.println(errMsg);
        view.drawFocus();
        // popup dialog with the error message
        view.showMessage(msg + "\n\n" + errMsg, "Exception Error", MessageCard.MessageType.ERROR);
    }

    /**
     * <h1>CALLS SYSTEM EXIT <br/><code>System.exit(0)</code></h1>
     *
     * @param cause
     */
    public void closeClient(String... cause) {
        if (clientSocket != null && clientSocket.isConnected()) {
            stopClient();
            if (cause.length > 0) {
                String msg = cause[0];
                String title = cause.length > 1 ? cause[1] : "Closing";

                view.showMessage(msg, title, MessageCard.MessageType.ERROR);
            }
        }

        log("Client Closed!");

        // close GUI
        closeGui();
        // close client
        System.exit(0);
    }

    public void stopClient() {
        clientSocket.close(); // will throw 'SocketException' and unblock I/O. see close() API
    }

    private void log(String str) {
        System.out.println(str);
    }

    public void closeGui() {
        clientRunOK = false;
        view.dispose();
    }

    // main
    public static void main(String[] args) {
        Client client = new Client();
        client.runClient();
    }

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

    public ClientMessagesHandler getMessagesHandler() {
        return msgHandler;
    }

    public void setMyColor(PlayerColor myColor) {
        this.myColor = myColor;
    }

    public AppSocket getClientSocket() {
        return clientSocket;
    }

    public void updateGameTime(Message message) {
        view.setGameTime(message.getGameTime());
    }

    public void updateByMove(Move move) {
        view.resetBackground();
        if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
            Piece piece = Piece.getPiece(move.getPromotingTo(), move.getMovingColor());
            view.setBtnPiece(move.getMovingFrom(), piece);
        }
        processGameStatus(move.getMoveEvaluation().getGameStatus());

        view.updateByMove(move);

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

//    private void initGame(Message message) {
//        myColor = message.getPlayerColor();
//        view.initGame(message.getGameTime(), message.getBoard(), myColor, message.getOtherPlayer());
//    }

    void hideQuestionPnl() {
        view.getSidePanel().askPlayerPnl.showPnl(false);
    }

    String login(Message loginMessage) {
        return login("", loginMessage);
    }

    private String login(String err, Message loginMessage) {

        Properties properties = dialogProperties("Login", "Login", err);
        this.loginInfo = view.showDialog(new LoginProcess(properties)).getLoginInfo();

        Message response = clientSocket.requestMessage(Message.returnLogin(loginInfo, loginMessage));

        if (this.loginInfo.getLoginType() == LoginType.CANCEL) {
            closeClient("");
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

    public void boardButtonPressed(ViewLocation loc) {
        if (possibleMoves != null) {
            view.resetBackground();
            view.enableAllSquares(false);
            Move completeMove = getMoveFromDest(loc);
            if (completeMove != null) {
                if (completeMove.getMoveFlag() == Move.MoveFlag.Promotion) {
                    completeMove.setPromotingTo(showPromotionDialog());
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

    private Move getMoveFromDest(ViewLocation clickedOn) {
        if (firstClickLoc == null)
            return null;
        return possibleMoves.stream()
                .filter(move ->
                        move.getMovingFrom().equals(firstClickLoc.originalLocation) &&
                                move.getMovingTo().equals(clickedOn.originalLocation))
                .findAny().orElse(null);
    }

    //todo change to new dialog system
    public PieceType showPromotionDialog() {
        ArrayList<DialogOption> options = new ArrayList<>();
        for (PieceType type : PieceType.CAN_PROMOTE_TO) {
            options.add(new DialogOption(IconManager.getPieceIcon(myColor, type), type));
        }
        DialogOption[] objectsOptions = new DialogOption[options.size()];
        for (int i = 0; i < options.size(); i++) {
            objectsOptions[i] = options.get(i);
        }
        return (PieceType) MyDialogs.showListDialog(view.getWin(), null, "Promote", null, objectsOptions).getKey();
    }

    private void returnMove(Move move) {
        clientSocket.writeMessage(Message.returnMove(move, lastGetMoveMsg));
    }

    public void resignBtnClicked() {
        clientSocket.writeMessage(new Message(MessageType.RESIGN));
    }

    public void offerDrawBtnClicked() {
        clientSocket.writeMessage(new Message(MessageType.OFFER_DRAW));
    }

    public View getView() {
        return view;
    }

    public void setLatestGetMoveMsg(Message lastGetMoveMsg) {
        this.lastGetMoveMsg = lastGetMoveMsg;
    }

    public void disconnectFromServer() {
        if (clientSocket != null && clientSocket.isConnected()) {
            Message response = clientSocket.requestMessage(Message.bye(""));
            view.showMessage(response.getSubject(), "disconnected", MessageCard.MessageType.INFO);
        }
        closeClient();
    }

    public void stopRunningTime() {
        view.getSidePanel().stopRunningTime();
    }

    public void startMyTime() {
        view.getSidePanel().startRunning(myColor);
    }

    public void startOpponentTime() {
        view.getSidePanel().startRunning(myColor.getOpponent());
    }

    public GameSettings showGameSettingsDialog() {
        String msg = "hello %s!\n%s".formatted(loginInfo.getUsername(), StrUtils.createTimeGreeting());
        Properties properties = dialogProperties(msg, "game selection");
        return view.showDialog(new GameSelect(properties)).getGameSettings();
    }

    public void changePassword() {
        ChangePassword changePassword = view.showDialog(new ChangePassword(dialogProperties("change password"), loginInfo.getPassword()));
        String pw = changePassword.getPassword();
        if (pw != null) {
            request(RequestBuilder.changePassword(), msg -> {
                if (msg != null && msg.getDBResponse().getStatus() == DBResponse.Status.SUCCESS) {
                    loginInfo.setPassword(pw);
                }
            }, null, pw);
        }

    }

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
            view.showDBResponse(msg.getDBResponse(), builder.getPostDescription(), builder.getName());

        });
    }

    public void request(PreMadeRequest request) {
        RequestBuilder builder = request.createBuilder();
        Properties properties = dialogProperties(builder.getPreDescription(), builder.getName());
//        Properties properties = new Properties(builder.getPreDescription(), builder.getName());
        CustomDialog customDialog = view.showDialog(new CustomDialog(properties, builder.getArgs()));
        Object[] args = customDialog.getResults();
        request(builder, null, args);
    }


}
