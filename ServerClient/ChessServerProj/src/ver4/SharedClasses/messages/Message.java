package ver4.SharedClasses.messages;

import ver4.SharedClasses.PlayerColor;
import ver4.SharedClasses.board_setup.Board;
import ver4.SharedClasses.evaluation.GameStatus;
import ver4.SharedClasses.moves.Move;
import ver4.SharedClasses.networking.LoginInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Message.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public class Message implements Serializable {
    private final MessageType messageType;
    private final String subject;
    private PlayerColor playerColor = null;
    private long[] clocks = null;
    private LoginInfo loginInfo = null;
    private Move move = null;
    private ArrayList<Move> possibleMoves = null;
    private Board board = null;
    private GameStatus gameStatus = null;
    private Boolean rematch = null;
    private ArrayList<Move> preMoves = null;

    public Message(MessageType messageType, String subject) {
        this.messageType = messageType;
        this.subject = subject;
    }

    public static Message askForLogin() {
        return new Message(MessageType.LOGIN, "Login");
    }

    public static Message returnLogin(LoginInfo loginInfo) {
        return new Message(MessageType.LOGIN, "") {{
            setLoginInfo(loginInfo);
        }};
    }

    public static Message welcomeMessage(String str, LoginInfo loginInfo) {
        return new Message(MessageType.WELCOME_MESSAGE, str) {{
            setLoginInfo(loginInfo);
        }};
    }

    public static Message initGame(Board board, ArrayList<Move> possibleMoves, PlayerColor player) {
        return new Message(MessageType.INIT_GAME, "Starting Game...") {{
            setBoard(board);
            setPossibleMoves(possibleMoves);
            setClocks(new long[]{0, 0});
            setPlayerColor(player);
        }};
    }

    public static Message askForMove(ArrayList<Move> possibleMoves) {
        return new Message(MessageType.GET_MOVE, "Its your turn!") {{
            setPossibleMoves(possibleMoves);
        }};
    }

    public static Message makeMove(Move move) {
        return new Message(MessageType.MAKE_MOVE, "") {{
            setMove(move);
        }};
    }

    public static Message waitForYourTurn(PlayerColor waitingFor) {
        return new Message(MessageType.WAIT_TURN, "Wait for " + waitingFor.getName() + " to make his move") {{
            setPreMoves(null);
        }};
    }

    public static Message gameOver(GameStatus gameStatus) {
        return new Message(MessageType.GAME_OVER, gameStatus.toString()) {{
            setGameStatus(gameStatus);
        }};
    }

    public static Message bye(String subject) {
        return new Message(MessageType.BYE, subject);
    }

    public static Message error(String err) {
        return new Message(MessageType.ERROR, err);
    }

    public static Message returnMove(Move move) {
        return new Message(MessageType.GET_MOVE, "") {{
            setMove(move);
        }};
    }

    public static Message rematch() {
        return new Message(MessageType.REMATCH, "");
    }

    public static Message rematch(boolean rematch) {
        return new Message(MessageType.REMATCH, "") {{
            setRematch(rematch);
        }};
    }

    public static Message isAlive() {
        return new Message(MessageType.IS_ALIVE, "Is alive");
    }

    public static Message alive() {
        return new Message(MessageType.ALIVE, "alive");
    }

    public ArrayList<Move> getPreMoves() {
        return preMoves;
    }

    public void setPreMoves(ArrayList<Move> preMoves) {
        this.preMoves = preMoves;
    }

    public Boolean getRematch() {
        return rematch;
    }

    public void setRematch(Boolean rematch) {
        this.rematch = rematch;
    }

    public boolean ignore() {
        return messageType == MessageType.ALIVE || messageType == MessageType.IS_ALIVE;
    }

    public String getSubject() {
        return subject;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public long[] getClocks() {
        return clocks;
    }

    public void setClocks(long[] clocks) {
        this.clocks = clocks;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public ArrayList<Move> getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(ArrayList<Move> possibleMoves) {
        ArrayList<Move> add = new ArrayList<>();
        for (Iterator<Move> iterator = possibleMoves.iterator(); iterator.hasNext(); ) {
            Move move = iterator.next();
            if (move.getClass().isAnonymousClass() || (move.getIntermediateMove() != null && move.getIntermediateMove().getClass().isAnonymousClass())) {
                add.add(Move.copyMove(move));
                iterator.remove();
            }
        }
        possibleMoves.addAll(add);
        this.possibleMoves = possibleMoves;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }


    @Override
    public String toString() {
        return "Message{" +
                "messageType=" + messageType +
                ", subject='" + subject + '\'' +
                ", playerColor=" + playerColor +
                ", clocks=" + Arrays.toString(clocks) +
                ", loginInfo=" + loginInfo +
                ", move=" + move +
                ", possibleMoves=" + possibleMoves +
                ", board=" + board +
                ", gameStatus=" + gameStatus +
                '}';
    }

}
