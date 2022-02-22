package ver4;

import ver4.SharedClasses.Location;
import ver4.SharedClasses.PlayerColor;
import ver4.SharedClasses.evaluation.GameStatus;
import ver4.SharedClasses.messages.Message;
import ver4.SharedClasses.messages.MessageType;
import ver4.SharedClasses.moves.BasicMove;
import ver4.SharedClasses.moves.Move;
import ver4.SharedClasses.networking.AppSocket;
import ver4.SharedClasses.networking.LoginInfo;
import ver4.SharedClasses.pieces.PieceType;
import ver4.SharedClasses.ui.dialogs.MyDialogs;
import ver4.SharedClasses.ui.dialogs.MyDialogs.DialogOption;
import ver4.view.IconManager;
import ver4.view.SidePanel;
import ver4.view.View;

import javax.swing.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Client דוגמה ללקוח צאט פשוט .
 * ---------------------------------------------------------------------------
 * by Ilan Perez (ilanperets@gmail.com) 20/10/2021
 */
public class Client {
    public static final String CLIENT_WIN_TITLE = "Chat Client";
    public static final int COLS = 8;
    public static final int ROWS = 8;

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
    private boolean isLoggedIn;
    private ArrayList<Move> possibleMoves = null;
    private Location firstClickLoc;

    /**
     * Constractor for Chat Client.
     */
    public Client() {
        view = new View(ROWS, this, PlayerColor.WHITE, 1000);
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
        view.enableAllSquares(false);
        view.setStatusLbl(message.getSubject());

        hideQuestionPnl();
        switch (message.getMessageType()) {
            case LOGIN -> login();
            case WELCOME_MESSAGE -> {
            }
            case INIT_GAME -> initGame(message);
            case WAIT_TURN -> waitTurn(message);
            case GET_MOVE -> getMove(message);
            case MAKE_MOVE -> makeMove(message);
            case GAME_OVER -> gameOver(message);
            case ERROR -> {
            }
            case REMATCH -> {
                view.getSidePanel().askPlayerPnl.ask(SidePanel.AskPlayer.Question.REMATCH);
            }
            case BYE -> {
                closeClient(message.getSubject());
            }
        }
    }

    private void gameOver(Message message) {
        processGameStatus(message.getGameStatus());
    }

    private void makeMove(Message message) {
        makeMove(message.getMove());
    }

    private void processGameStatus(GameStatus gameStatus) {
        if (gameStatus.isCheck()) {
            view.inCheck(gameStatus.getCheckedKingLoc());
        }
        if (gameStatus.isGameOver()) {
            view.enableAllSquares(false);
            PlayerColor iconClr = myColor;
            if (gameStatus.getWinningColor() != null) {
                iconClr = gameStatus.getWinningColor();
            }
            view.showMessage(gameStatus.toString(), "Game Over", IconManager.getGameOverIcon(gameStatus, iconClr));
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
        view.initGame(message.getClocks(), message.getBoard(), myColor);
    }

    private void unlockPossibleMoves() {
        hideQuestionPnl();
        if (possibleMoves != null) {
            if (canClaimThreefold()) {
                view.getSidePanel().askPlayerPnl.ask(SidePanel.AskPlayer.Question.Threefold);
            }
            enableViewBtns(possibleMoves.stream()
                    .map(BasicMove::getMovingFrom)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
    }

    private void hideQuestionPnl() {
        view.getSidePanel().askPlayerPnl.showPnl(false);
    }

    private boolean canClaimThreefold() {
        return possibleMoves.
                stream()
                .anyMatch(move -> move.getThreefoldStatus() == Move.ThreefoldStatus.CAN_CLAIM);
    }

    private void makeMove(Move move) {
        view.resetBackground();
        if (move.getMoveFlag() == Move.MoveFlag.Promotion) {
            PlayerColor movingColor = move.getMovingColor();
            view.setBtnIcon(move.getMovingFrom(), IconManager.getPieceIcon(movingColor, move.getPromotingTo()));
        }
        if (move.getMoveEvaluation().isCheck()) {
            processGameStatus(move.getMoveEvaluation().getGameStatus());
        }
//        assert move.getMoveEvaluation() != null;
//        System.out.println("Move Eval = " + move.getMoveEvaluation());
//        processGameStatus(move.getMoveEvaluation().getGameStatus());
        view.makeMove(move);
    }

    private void setupClient() {
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
            clientSocket = new AppSocket(new Socket(serverIP, serverPort));

            isLoggedIn = false;

            clientSetupOK = true;
        } catch (Exception exp) {
            clientSetupOK = false;
            String serverAddress = serverIP + ":" + serverPort;
            log("Client can't connect to Server(" + serverAddress + ")", exp, view.getWin());
        }
    }

    private String login() {
        return login("");
    }

    private String login(String err) {
        isLoggedIn = false;
        LoginInfo loginInfo = Login.showLoginDialog(err, view.getWin());

        clientSocket.writeMessage(Message.returnLogin(loginInfo));
        if (loginInfo.isCancel()) {
            closeClient("");
            return null;
        }
        Message response = clientSocket.readMessage();
        if (response == null) {
            return login("Error reading response from server");
        }
        if (response.getMessageType() == MessageType.ERROR) {
            return login(response.getSubject());
        }

        if (response.getMessageType() == MessageType.WELCOME_MESSAGE) {
            loginInfo = response.getLoginInfo();
        }
        String username = loginInfo.getUsername();
        isLoggedIn = true;
        setTitle(username);
        return response.getSubject();
    }

    private void setTitle(String username) {
        view.setTitle(CLIENT_WIN_TITLE + " (" + clientSocket.getLocalAddress() + ") " + username);
    }


    private void enableViewBtns(ArrayList<Location> list) {
        view.enableSquares(list);
    }

    public void stopClient() {
        clientSocket.close(); // will throw 'SocketException' and unblock I/O. see close() API
    }

    private void closeClient() {
        closeClient("The connection with the Server is LOST!\nClient will close now...", "Chat Client Error");
    }


    private void closeClient(String... cause) {
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
                unlockPossibleMoves();
                view.highlightPath(possibleMoves.stream()
                        .filter(move -> move.getMovingFrom().equals(loc))
                        .collect(Collectors.toCollection(ArrayList::new)));
            }
        }
    }

    private void returnMove(Move move) {
        clientSocket.writeMessage(Message.returnMove(move));
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

    private void log(String str) {
        System.out.println(str);
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

    public void claimedRepetitionDraw() {
        assert canClaimThreefold();
        returnMove(Move.threefoldClaim());
    }

    public void answeredQuestionNo(SidePanel.AskPlayer.Question question) {
        switch (question) {
            case Threefold -> {
            }
            case REMATCH -> {
                clientSocket.writeMessage(Message.rematch(false));
            }
        }
    }

    public void answeredQuestionYes(SidePanel.AskPlayer.Question question) {
        switch (question) {
            case Threefold -> {
                claimedRepetitionDraw();
            }
            case REMATCH -> {
                clientSocket.writeMessage(Message.rematch(true));
            }
        }
    }
}
