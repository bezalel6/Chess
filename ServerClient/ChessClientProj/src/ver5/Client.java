package ver5;

import ver5.SharedClasses.*;
import ver5.SharedClasses.evaluation.GameStatus;
import ver5.SharedClasses.messages.Message;
import ver5.SharedClasses.messages.MessageType;
import ver5.SharedClasses.moves.Move;
import ver5.SharedClasses.pieces.Piece;
import ver5.SharedClasses.pieces.PieceType;
import ver5.SharedClasses.ui.dialogs.MyDialogs;
import ver5.SharedClasses.ui.dialogs.MyDialogs.DialogOption;
import ver5.view.IconManager;
import ver5.view.View;
import ver5.view.dialogs.LoginProcess.LoginProcess;
import ver5.view.dialogs.game_select.GameSelect;

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

    private ClientSocket clientSocket;
    private PlayerColor myColor;
    private ArrayList<Move> possibleMoves = null;
    private Location firstClickLoc;

    /**
     * Constractor for Chat Client.
     */
    public Client() {
        this(true);
    }

    public Client(boolean visible) {
        view = new View(this, visible);
        setupClient();
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

    // main
    public static void main(String[] args) {
        Client client = new Client();
        client.runClient();

        System.out.println("**** Client main() finished! ****");
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
                closeClient();
                return;
            }

            // get server IP & PORT from input string
            serverAddress = serverAddress.replace(" ", ""); // remove all spaces
            serverIP = serverAddress.substring(0, serverAddress.indexOf(":"));
            serverPort = Integer.parseInt(serverAddress.substring(serverAddress.indexOf(":") + 1));

            // Setup connection to SERVER Address
//            socketToServer = new Socket(serverIP, serverPort);
            clientSocket = new ClientSocket(serverPort, serverIP);
            clientSocket.getAliveSocket().start(() -> {
                closeClient("disconnected from server");
            });

            clientSetupOK = true;
        } catch (Exception exp) {
            clientSetupOK = false;
            String serverAddress = serverIP + ":" + serverPort;
            log("Client can't connect to Server(" + serverAddress + ")", exp, view.getWin());
        }
    }

    private void closeClient() {
        closeClient("The connection with the Server is LOST!\nClient will close now...", "Chat Client Error");
    }

    public void closeClient(String... cause) {
        if (clientSocket != null && clientSocket.isConnected()) {
            if (cause.length > 0) {
                String msg = cause[0];
                String title = cause.length > 1 ? cause[1] : "Closing";
                JOptionPane.showMessageDialog(view.getWin(), msg, title, JOptionPane.ERROR_MESSAGE);
            }
            stopClient();
        }

        log("Client Closed!");

        // close GUI
        view.getWin().dispose();

        // close client
//        System.exit(0);
    }

    public void stopClient() {
        clientSocket.close(); // will throw 'SocketException' and unblock I/O. see close() API
    }

    private void log(String str) {
        System.out.println(str);
    }

    public void runClient() {
        if (clientSetupOK) {
            log("Connected to Server(" + clientSocket.getRemoteAddress() + ")");
            log("CLIENT(" + clientSocket.getLocalAddress() + ") Setup & Running!\n");
//            setChatMsgSendEnable(clientSocket.isLoggedIn());
            firstClickLoc = null;
            clientRunOK = true;

            // loop while client running OK
            while (clientRunOK) {
                // wait for a message from server or null if socket closed
                Message msg = clientSocket.readMessage();

                if (msg == null)
                    clientRunOK = false;
                else
                    processMsgFromServer(msg);
            }
        }
        closeClient();

        System.out.println("**** runClient() finished! ****");
    }

    private void processMsgFromServer(Message message) {
        if (message.ignore())
            return;

        if (message.getMessageType() != MessageType.QUESTION)
            view.setStatusLbl(message.getSubject());

        updateGameTime(message);
        hideQuestionPnl();

        switch (message.getMessageType()) {
            case LOGIN -> {
                String username = login();
                view.setUsername(username);
            }
            case WELCOME_MESSAGE -> {
            }
            case WAIT_FOR_MATCH -> {
            }
            case INIT_GAME -> initGame(message);
            case WAIT_TURN -> waitTurn(message);
            case GET_MOVE -> getMove(message);
            case UPDATE_BY_MOVE -> makeMove(message);
            case GAME_OVER -> gameOver(message);
            case ERROR -> {
            }
            case QUESTION -> {
                question(message);
            }
            case BYE -> {
                closeClient(message.getSubject());
            }
            case GET_GAME_SETTINGS -> {
                GameSettings gameSettings = GameSelect.showGameSelectionDialog(clientSocket);
                clientSocket.writeMessage(Message.returnGameSettings(gameSettings));
            }
            default -> throw new IllegalStateException("Unexpected value: " + message.getMessageType());
        }
    }

    private void question(Message message) {
        view.getSidePanel().askPlayerPnl.ask(message.getQuestion(), answer -> {
            message.getQuestion().setAnswer(answer);
            clientSocket.writeMessage(Message.question(message.getQuestion()));
        });
    }

    private void updateGameTime(Message message) {
        view.setGameTime(message.getGameTime());
    }

    private void gameOver(Message message) {
//        processGameStatus(message.getGameStatus());
        GameStatus gameStatus = message.getGameStatus();
//        view.enableAllSquares(false);
//        PlayerColor iconClr = myColor;
//        if (gameStatus.getWinningColor() != null) {
//            iconClr = gameStatus.getWinningColor();
//        }
        view.gameOver(gameStatus.getDetailedStr());
//        view.showMessage(gameStatus.toString(), "Game Over", IconManager.getGameOverIcon(gameStatus, iconClr));
    }

    private void makeMove(Message message) {
        makeMove(message.getMove());
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

    private void unlockMovableSquares(Message message) {
        possibleMoves = message.getPossibleMoves();
        unlockPossibleMoves();
    }

    private void waitTurn(Message message) {

    }

    private void initGame(Message message) {
        myColor = message.getPlayerColor();
        view.initGame(message.getGameTime(), message.getBoard(), myColor, message.getOtherPlayer());
    }

    private void makeMove(Move move) {
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

    private String login() {
        return login("");
    }

    private String login(String err, LoginType... _loginType) {
        LoginType loginType = _loginType.length == 0 ? null : _loginType[0];
        LoginInfo loginInfo = LoginProcess.loginProcess(err, loginType, clientSocket);

        clientSocket.writeMessage(Message.returnLogin(loginInfo));
        if (loginInfo.getLoginType() == LoginType.CANCEL) {
            closeClient("");
            return null;
        }
        Message response = clientSocket.readMessage();
        if (response == null) {
            return login("Error reading response from server");
        }
        if (response.getMessageType() == MessageType.ERROR) {
            return login(response.getSubject(), loginInfo.getLoginType());
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
        clientSocket.writeMessage(Message.returnMove(move));
    }

    private void unlockPossibleMoves() {
        hideQuestionPnl();
        view.enableSources(possibleMoves);
    }

    private void hideQuestionPnl() {
        view.getSidePanel().askPlayerPnl.showPnl(false);
    }

    public void statisticsBtnClicked() {
        clientSocket.getBackgroundSocket().writeMessage(Message.askForStatistics());
        Message msg = clientSocket.getBackgroundSocket().readMessage();
        view.showStatistics(msg.getPlayerStatistics());
    }

    public void resignBtnClicked() {
        clientSocket.getBackgroundSocket().writeMessage(new Message(MessageType.RESIGN));
    }

    public void addTimeButtonClicked() {
        clientSocket.getBackgroundSocket().writeMessage(new Message(MessageType.ADD_TIME));

    }

    public void offerDrawBtnClicked() {
        clientSocket.getBackgroundSocket().writeMessage(new Message(MessageType.OFFER_DRAW));
    }

    public View getView() {
        return view;
    }
}
