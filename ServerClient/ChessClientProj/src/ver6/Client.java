package ver6;

import ver6.SharedClasses.Location;
import ver6.SharedClasses.LoginInfo;
import ver6.SharedClasses.LoginType;
import ver6.SharedClasses.PlayerColor;
import ver6.SharedClasses.evaluation.GameStatus;
import ver6.SharedClasses.messages.Message;
import ver6.SharedClasses.messages.MessageType;
import ver6.SharedClasses.moves.Move;
import ver6.SharedClasses.networking.AppSocket;
import ver6.SharedClasses.networking.MyErrors;
import ver6.SharedClasses.pieces.Piece;
import ver6.SharedClasses.pieces.PieceType;
import ver6.SharedClasses.ui.dialogs.MyDialogs;
import ver6.SharedClasses.ui.dialogs.MyDialogs.DialogOption;
import ver6.view.IconManager;
import ver6.view.View;
import ver6.view.dialogs.LoginProcess.LoginProcess;
import ver6.view.dialogs.MyDialog;

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
    private ArrayList<MyDialog> displayedDialogs;

    /**
     * Constractor for Chat Client.
     */
    public Client() {
        this(true);
    }

    public Client(boolean visible) {
        view = new View(this, visible);
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

    public void closeClient(String... cause) {
        if (clientSocket != null && clientSocket.isConnected()) {
            if (cause.length > 0) {
                String msg = cause[0];
                String title = cause.length > 1 ? cause[1] : "Closing";

                //in its own thread so that the socket can close immediately
//                new Thread(() -> {
                MyDialogs.showErrorMessage(null, msg, title);
//                }).start();
            }
            stopClient();
        }

        log("Client Closed!");

        // close GUI
        closeView();
        // close client
//        System.exit(0);
    }

    public void stopClient() {
        clientSocket.close(); // will throw 'SocketException' and unblock I/O. see close() API
    }

    private void log(String str) {
        System.out.println(str);
    }

    public void closeView() {
        clientRunOK = false;
        view.disposeWin();
        for (MyDialog dialog : displayedDialogs) {
            dialog.closeNow();
        }
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
            try {
                clientSocket.setHandlerAndStart(new ServerMessagesHandler(this, view));
            } catch (MyErrors e) {
                disconnectedFromServer();
            }
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
        if (move.getMoveEvaluation().isCheck()) {
            processGameStatus(move.getMoveEvaluation().getGameStatus());
        }
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

    private void initGame(Message message) {
        myColor = message.getPlayerColor();
        view.initGame(message.getGameTime(), message.getBoard(), myColor, message.getOtherPlayer());
    }

    String login(Message loginMessage) throws MyErrors {
        return login("", loginMessage);
    }

    private String login(String err, Message loginMessage, LoginType... _loginType) throws MyErrors {
        LoginType loginType = _loginType.length == 0 ? null : _loginType[0];

        LoginInfo loginInfo = showDialog(LoginProcess.create(err, loginType, clientSocket)).getLoginInfo();

        Message response = clientSocket.requestMessage(Message.returnLogin(loginInfo, loginMessage));

        if (loginInfo.getLoginType() == LoginType.CANCEL) {
            closeClient("");
            return null;
        }

        if (response == null) {
            return login("Error reading response from server", loginMessage);
        }
        if (response.getMessageType() == MessageType.ERROR) {
            return login(response.getSubject(), response, loginInfo.getLoginType());
        }
        if (response.getMessageType() == MessageType.WELCOME_MESSAGE) {
            loginInfo = response.getLoginInfo();
        }
        return loginInfo.getUsername();
    }

    public <T extends MyDialog> T showDialog(T dialog) {
        displayedDialogs.add(dialog);
        dialog.start();
        if (clientRunOK)
            displayedDialogs.remove(dialog);
        return dialog;
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

    public void statisticsBtnClicked() {
        new Thread(() -> {
            try {
                Message stats = clientSocket.requestMessage(Message.askForStatistics());
                view.showStatistics(stats.getPlayerStatistics());
            } catch (MyErrors e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void resignBtnClicked() {
        clientSocket.writeMessage(new Message(MessageType.RESIGN));
    }

    public void addTimeButtonClicked() {
        clientSocket.writeMessage(new Message(MessageType.ADD_TIME));

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
}