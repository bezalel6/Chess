package ver5.SharedClasses.messages;

import ver5.SharedClasses.*;
import ver5.SharedClasses.board_setup.Board;
import ver5.SharedClasses.evaluation.GameStatus;
import ver5.SharedClasses.moves.Move;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Message.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public class Message implements Serializable {
    private final MessageType messageType;
    private final String subject;
    private String username = null;
    private String otherPlayer = null;
    private Boolean available = null;
    private PlayerColor playerColor = null;
    private GameTime gameTime = null;
    private LoginInfo loginInfo = null;
    private Move move = null;
    private GameSettings gameSettings = null;
    private ArrayList<Move> possibleMoves = null;
    private Board board = null;
    private GameStatus gameStatus = null;
    private Question question = null;
    private ArrayList<Move> preMoves = null;
    private Collection<SavedGame> joinableGames = null;
    private Collection<SavedGame> resumableGames = null;
    private Collection<SavedGame> requestedGames = null;
    private PlayerStatistics playerStatistics = null;

    public Message(MessageType messageType) {
        this(messageType, null);
    }

    public Message(MessageType messageType, String subject) {
        this.messageType = messageType;
        this.subject = subject;
    }

    public static Message askForLogin() {
        return new Message(MessageType.LOGIN, "Login");
    }

    public static Message returnLogin(LoginInfo loginInfo) {
        return new Message(MessageType.LOGIN) {{
            setLoginInfo(loginInfo);
        }};
    }

    public static Message welcomeMessage(String str, LoginInfo loginInfo) {
        return new Message(MessageType.WELCOME_MESSAGE, str) {{
            setLoginInfo(loginInfo);
        }};
    }

    public static Message initGame(Board board, String opponent, PlayerColor player, GameTime gameTime) {
        return new Message(MessageType.INIT_GAME, "Starting Game...") {{
            setBoard(board);
            setOtherPlayer(opponent);
            setGameTime(gameTime);
            setPlayerColor(player);
        }};
    }

    public static Message askForMove(ArrayList<Move> possibleMoves, GameTime gameTime) {
        return new Message(MessageType.GET_MOVE, "Its your turn!") {{
            setPossibleMoves(possibleMoves);
            setGameTime(gameTime);
        }};
    }

    public static Message makeMove(Move move, GameTime gameTime) {
        return new Message(MessageType.UPDATE_BY_MOVE) {{
            setMove(move);
            setGameTime(gameTime);
        }};
    }

    public static Message waitForYourTurn(String waitingForName, GameTime gameTime) {
        waitingForName = StrUtils.dontCapWord(waitingForName);
        return new Message(MessageType.WAIT_TURN, "Wait for " + waitingForName + " to make a move") {{
            setPreMoves(null);
            setGameTime(gameTime);
        }};
    }

    public static Message waitForMatch() {
        return new Message(MessageType.WAIT_FOR_MATCH, "Wait for match");
    }

    public static Message gameOver(GameStatus gameStatus) {
        return new Message(MessageType.GAME_OVER, gameStatus.toString()) {{
            setGameStatus(gameStatus);
        }};
    }

    public static Message askForGameSettings(Collection<SavedGame> joinableGames, Collection<SavedGame> resumableGames) {
        return new Message(MessageType.GET_GAME_SETTINGS, "Choose game settings") {{
            setJoinableGames(joinableGames);
            setResumableGames(resumableGames);
        }};
    }

    public static Message askForResumableGames() {
        return new Message(MessageType.RESUMABLE_GAMES);
    }

    public static Message returnResumableGames(Collection<SavedGame> resumableGames) {
        return new Message(MessageType.RESUMABLE_GAMES) {{
            setResumableGames(resumableGames);
        }};
    }

    public static Message askForJoinableGames() {
        return new Message(MessageType.JOINABLE_GAMES);
    }

    public static Message returnJoinableGames(Collection<SavedGame> joinableGames) {
        return new Message(MessageType.JOINABLE_GAMES) {{
            setResumableGames(joinableGames);
        }};
    }

    public static Message returnGameSettings(GameSettings gameSettings) {
        return new Message(MessageType.GET_GAME_SETTINGS) {{
            setGameSettings(gameSettings);
        }};
    }

    public static Message interrupt() {
        return new Message(MessageType.INTERRUPT);
    }

    public static Message stopRead() {
        return new Message(MessageType.STOP_READ);
    }

    public static Message bye(String subject) {
        return new Message(MessageType.BYE, subject);
    }

    public static Message error(String err) {
        return new Message(MessageType.ERROR, err);
    }

    public static Message returnMove(Move move) {
        return new Message(MessageType.GET_MOVE) {{
            setMove(move);
        }};
    }

    public static Message question(Question question) {
        return new Message(MessageType.QUESTION) {{
            setQuestion(question);
        }};
    }

    public static Message checkUsernameAvailability(String username) {
        return new Message(MessageType.USERNAME_AVAILABILITY) {{
            setUsername(username);
        }};
    }

    public static Message returnUsernameAvailable(boolean available) {
        return new Message(MessageType.USERNAME_AVAILABILITY) {{
            setAvailable(available);
        }};
    }

    public static Message askForStatistics() {
        return new Message(MessageType.PLAYERS_STATISTICS);
    }

    public static Message returnPlayersStatistics(PlayerStatistics playerStatistics) {
        return new Message(MessageType.PLAYERS_STATISTICS) {{
            setPlayerStatistics(playerStatistics);
        }};
    }

    public static Message isAlive() {
        return new Message(MessageType.IS_ALIVE, "Is alive");
    }

    public static Message alive() {
        return new Message(MessageType.ALIVE, "alive");
    }

    public static Message syncList(SyncedListType syncedListType, Collection<SavedGame> values) {
        return new Message(syncedListType.messageType) {{
            setRequestedGames(values);
        }};
    }

    public String getOtherPlayer() {
        return otherPlayer;
    }

    public void setOtherPlayer(String otherPlayer) {
        this.otherPlayer = otherPlayer;
    }

    public PlayerStatistics getPlayerStatistics() {
        return playerStatistics;
    }

    public void setPlayerStatistics(PlayerStatistics playerStatistics) {
        this.playerStatistics = playerStatistics;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Collection<SavedGame> getRequestedGames() {
        return requestedGames;
    }

    public void setRequestedGames(Collection<SavedGame> requestedGames) {
        this.requestedGames = requestedGames;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Collection<SavedGame> getResumableGames() {
        return resumableGames;
    }

    public void setResumableGames(Collection<SavedGame> resumableGames) {
        this.resumableGames = resumableGames;
        setRequestedGames(resumableGames);
    }

    public Collection<SavedGame> getJoinableGames() {
        return joinableGames;
    }

    public void setJoinableGames(Collection<SavedGame> joinableGames) {
        this.joinableGames = joinableGames;
        setRequestedGames(joinableGames);
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    public ArrayList<Move> getPreMoves() {
        return preMoves;
    }

    public void setPreMoves(ArrayList<Move> preMoves) {
        this.preMoves = preMoves;
    }

    public boolean ignore() {
        return messageType.ignore;
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


    public GameTime getGameTime() {
        return gameTime;
    }

    public void setGameTime(GameTime gameTime) {
        this.gameTime = gameTime;
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
//            assert !(move instanceof Castling);
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
                ", username='" + username + '\'' +
                ", otherPlayer='" + otherPlayer + '\'' +
                ", available=" + available +
                ", playerColor=" + playerColor +
                ", gameTime=" + gameTime +
                ", loginInfo=" + loginInfo +
                ", move=" + move +
                ", gameSettings=" + gameSettings +
                ", possibleMoves=" + possibleMoves +
                ", board=" + board +
                ", gameStatus=" + gameStatus +
                ", question=" + question +
                ", preMoves=" + preMoves +
                ", joinableGames=" + joinableGames +
                ", resumableGames=" + resumableGames +
                ", requestedGames=" + requestedGames +
                ", playerStatistics=" + playerStatistics +
                '}';
    }
}
