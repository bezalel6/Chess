package ver10;

import ver10.SharedClasses.DBActions.Arg.Arg;
import ver10.SharedClasses.DBActions.Arg.ArgType;
import ver10.SharedClasses.DBActions.RequestBuilder;
import ver10.SharedClasses.*;
import ver10.SharedClasses.Utils.StrUtils;
import ver10.SharedClasses.evaluation.GameStatus;
import ver10.SharedClasses.messages.Message;
import ver10.SharedClasses.messages.MessageType;
import ver10.SharedClasses.moves.Move;
import ver10.SharedClasses.networking.AppSocket;
import ver10.SharedClasses.pieces.Piece;
import ver10.SharedClasses.pieces.PieceType;
import ver10.SharedClasses.ui.dialogs.MyDialogs;
import ver10.SharedClasses.ui.dialogs.MyDialogs.DialogOption;
import ver10.view.Dialog.Dialog;
import ver10.view.Dialog.DialogProperties;
import ver10.view.Dialog.Dialogs.GameSelection.GameSelect;
import ver10.view.Dialog.Dialogs.LoginProcess.LoginProcess;
import ver10.view.Dialog.Dialogs.SimpleDialogs.CustomDialog;
import ver10.view.IconManager.IconManager;
import ver10.view.View;

import javax.swing.*;
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

    // for GUI
    private View view;

    // for Client
    private boolean clientSetupOK, clientRunOK;
    private int serverPort;
    private String serverIP;

    private AppSocket clientSocket;
    private PlayerColor myColor;
    private ArrayList<Move> possibleMoves = null;
    private Location firstClickLoc;
    private Message lastGetMoveMsg;
    private ArrayList<Dialog> displayedDialogs;
    private LoginInfo loginInfo;

    /**
     * Constractor for Chat Client.
     */

    public Client() {
        view = new View(this);
        displayedDialogs = new ArrayList<>();
        setupClient();
    }

    public void setupClient() {
        try {
            // set the Server Adress (DEFAULT IP&PORT)
            serverPort = SERVER_DEFAULT_PORT;
            serverIP = InetAddress.getLocalHost().getHostAddress(); // IP of this computer

            // get Server Address from user
            String serverAddress = JOptionPane.showInputDialog(view.getWin(), "Enter SERVER Address [IP : PORT]", serverIP + " : " + serverPort);

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
            log("Client can't connect to Server(" + serverAddress + ")", exp, view.getWin());
        }
    }

    public void disconnectedFromServer() {
        closeClient("The connection with the Server is LOST!\nClient will close now...", "Chat Client Error");
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

                MyDialogs.showErrorMessage(null, msg, title);
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
        for (Dialog dialog : displayedDialogs) {
            dialog.closeNow();
        }
        view.dispose();
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
            firstClickLoc = null;
            clientSocket.setMessagesHandler(new ServerMessagesHandler(this, view));
            clientSocket.startReading();
            view.connectedToServer();
//            while (clientSocket.isConnected() && !clientSocket.isClosed()) ;
        }
//        closeClient();

    }

    public ServerMessagesHandler getMessagesHandler() {
        return (ServerMessagesHandler) clientSocket.getMessagesHandler();
    }

    public void setMyColor(PlayerColor myColor) {
        this.myColor = myColor;
    }

    public AppSocket getClientSocket() {
        return clientSocket;
    }

    void updateGameTime(Message message) {
        view.setGameTime(message.getGameTime());
    }

    void updateByMove(Move move) {
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

    private void getMove(Message message) {
        unlockMovableSquares(message);
        view.getWin().requestFocus();
    }

    void unlockMovableSquares(Message message) {
        possibleMoves = message.getPossibleMoves();
        unlockPossibleMoves();
    }

    private void unlockPossibleMoves() {
        hideQuestionPnl();
        view.enableSources(possibleMoves);
    }

    void hideQuestionPnl() {
        view.getSidePanel().askPlayerPnl.showPnl(false);
    }

//    private void initGame(Message message) {
//        myColor = message.getPlayerColor();
//        view.initGame(message.getGameTime(), message.getBoard(), myColor, message.getOtherPlayer());
//    }

    String login(Message loginMessage) {
        return login("", loginMessage);
    }

    private String login(String err, Message loginMessage) {

        loginInfo = ((LoginProcess) showDialog(Dialog.DialogType.LoginProcess, new DialogProperties("Login", err, "Login"))).getLoginInfo();

        Message response = clientSocket.requestMessage(Message.returnLogin(loginInfo, loginMessage));

        if (loginInfo.getLoginType() == LoginType.CANCEL) {
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
            loginInfo = response.getLoginInfo();
        }
        return loginInfo.getUsername();
    }

    public void boardButtonPressed(Location loc) {
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
                view.colorCurrentPiece(firstClickLoc);
                unlockPossibleMoves();
                view.highlightPath(possibleMoves.stream()
                        .filter(move -> move.getMovingFrom().equals(loc))
                        .collect(Collectors.toCollection(ArrayList::new)));
            }
        }
    }

    private Move getMoveFromDest(Location clickedOn) {
        if (firstClickLoc == null)
            return null;
        return possibleMoves.stream()
                .filter(move ->
                        move.getMovingFrom().equals(firstClickLoc) &&
                                move.getMovingTo().equals(clickedOn))
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
        if (clientSocket != null) {
            Message response = clientSocket.requestMessage(Message.bye(""));
            view.showMessage(response.getSubject(), "", null);
        }
        closeClient();
    }

    public void request(RequestBuilder.PreMadeRequest request) {
        RequestBuilder builder = request.createBuilder();
        DialogProperties properties = new DialogProperties(builder.getPreDescription(), builder.getName());
        CustomDialog customDialog = showDialog(new CustomDialog(view.getWin(), properties, builder.getArgs()));
        Object[] args = customDialog.getResults();

        if (args == null) {
            return;
        }
        for (int i = 0; i < args.length; i++) {
            Arg arg = builder.getArgs()[i];
            Object argVal = args[i];
            if (!arg.argType.isUserInput) {
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
            view.showDBResponse(msg.getDBResponse(), builder.getPostDescription(), builder.getName());
        });
    }

    public <T extends Dialog> T showDialog(T dialog) {
        displayedDialogs.add(dialog);
        dialog.start(displayedDialogs::remove);
        return dialog;
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
        DialogProperties properties = new DialogProperties(msg, "Game Selection");
        return ((GameSelect) showDialog(Dialog.DialogType.GameSelection, properties)).getGameSettings();
    }

    public <T extends Dialog> T showDialog(Dialog.DialogType dialogType, DialogProperties properties) {
        Dialog d = Dialog.createDialog(view.getWin(), dialogType, clientSocket, properties);
        return (T) showDialog(d);
    }
}
