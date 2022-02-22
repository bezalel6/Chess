package ver11.SharedClasses.messages;

import ver11.SharedClasses.DBActions.DBRequest;
import ver11.SharedClasses.DBActions.DBResponse;
import ver11.SharedClasses.*;
import ver11.SharedClasses.Sync.SyncedItems;
import ver11.SharedClasses.Utils.StrUtils;
import ver11.SharedClasses.board_setup.Board;
import ver11.SharedClasses.evaluation.GameStatus;
import ver11.SharedClasses.moves.Move;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/**
 * Message.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public class Message implements Serializable {
    private final static IDsGenerator messagesIds = new IDsGenerator();
    public final String messageID;
    private final MessageType messageType;
    private final String subject;
    private String respondingToMsgId;
    private String username = null;
    private String otherPlayer = null;
    private Boolean available = null;
    private ArrayList<String> usernameSuggestions = null;
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
    private SyncedItems<?>[] syncedLists = null;
    private Stack<Move> moveStack = null;
    private DBResponse dbResponse = null;
    private DBRequest dbRequest = null;
    private boolean hideQuestion;

    public Message(MessageType messageType) {
        this(messageType, null, null);
    }

    public Message(MessageType messageType, String subject, String respondingToMsgId) {
        this.messageType = messageType;
        this.subject = subject;
        this.respondingToMsgId = respondingToMsgId;
        this.messageID = messagesIds.generate();
        this.hideQuestion = messageType.hideQuestion();
    }

    public Message(MessageType messageType, Message respondingTo) {
        this(messageType, null, respondingTo.messageID);
    }

    public Message(MessageType messageType, String subject) {
        this(messageType, subject, null);
    }

    public static Message askForLogin() {
        return new Message(MessageType.LOGIN, "Login");
    }

    public static Message returnLogin(LoginInfo loginInfo, Message respondingTo) {
        return new Message(MessageType.LOGIN, respondingTo) {{
            setLoginInfo(loginInfo);
        }};
    }

    public static Message welcomeMessage(String str, LoginInfo loginInfo) {
        return new Message(MessageType.WELCOME_MESSAGE, str) {{
            setLoginInfo(loginInfo);
        }};
    }

    public static Message initGame(Board board, String opponent, PlayerColor player, GameTime gameTime, Stack<Move> moveStack) {
        return new Message(MessageType.INIT_GAME, "Starting Game...") {
            {
                setBoard(board);
                setMoveStack(moveStack);
                setOtherPlayer(opponent);
                setGameTime(gameTime);
                setPlayerColor(player);
            }
        };
    }

    public static Message askForMove(ArrayList<Move> possibleMoves, GameTime gameTime) {
        return new Message(MessageType.GET_MOVE, "Its your turn!") {{
            setPossibleMoves(possibleMoves);
            setGameTime(gameTime);
        }};
    }

    public static Message updateByMove(Move move, GameTime gameTime) {
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

    public static Message askForGameSettings(SyncedItems joinableGames, SyncedItems resumableGames) {
        return new Message(MessageType.GET_GAME_SETTINGS) {{
            setSyncedLists(joinableGames, resumableGames);
        }};
    }

    public static Message returnGameSettings(GameSettings gameSettings, Message respondingTo) {
        return new Message(MessageType.GET_GAME_SETTINGS, respondingTo) {{
            setGameSettings(gameSettings);
        }};
    }

    public static Message interrupt() {
        return new Message(MessageType.INTERRUPT);
    }

    public static Message bye(String subject) {
        return new Message(MessageType.BYE, subject);
    }

    public static Message error(String err) {
        return new Message(MessageType.ERROR, err);
    }

    public static Message returnMove(Move move, Message respondingTo) {
        return new Message(MessageType.GET_MOVE, respondingTo) {{
            setMove(move);
        }};
    }

    public static Message answerQuestion(Question question, Message respondingTo) {
        return new Message(MessageType.QUESTION, respondingTo) {{
            setQuestion(question);
        }};
    }

    public static Message askQuestion(Question question) {
        return new Message(MessageType.QUESTION) {{
            setQuestion(question);
        }};
    }

    public static Message checkUsernameAvailability(String username) {
        return new Message(MessageType.USERNAME_AVAILABILITY) {{
            setUsername(username);
        }};
    }

    public static Message returnUsernameNotAvailable(ArrayList<String> usernameSuggestions, Message request) {
        return new Message(MessageType.USERNAME_AVAILABILITY, request) {{
            setUsernameSuggestions(usernameSuggestions);
            setAvailable(false);
        }};
    }

    public static Message returnUsernameAvailable(Message request) {
        return new Message(MessageType.USERNAME_AVAILABILITY, request) {{
            setAvailable(true);
        }};
    }

    public static Message dbRequest(DBRequest dbRequest) {
        return new Message(MessageType.DB_REQUEST) {{
            setDbRequest(dbRequest);
        }};
    }

    public void setDbRequest(DBRequest dbRequest) {
        this.dbRequest = dbRequest;
    }

    public static Message returnDBResponse(DBResponse DBResponse, Message request) {
        return new Message(MessageType.DB_RESPONSE, request) {{
            setDBResponse(DBResponse);
        }};
    }

    public static Message isAlive() {
        return new Message(MessageType.IS_ALIVE);
    }

    public static Message alive() {
        return new Message(MessageType.ALIVE);
    }

    public static Message syncLists(SyncedItems... syncedList) {
        return new Message(MessageType.UPDATE_SYNCED_LIST) {{
            setSyncedLists(syncedList);
        }};
    }

    public DBRequest getDBRequest() {
        return dbRequest;
    }

    public ArrayList<String> getUsernameSuggestions() {
        return usernameSuggestions;
    }

    public void setUsernameSuggestions(ArrayList<String> usernameSuggestions) {
        this.usernameSuggestions = usernameSuggestions;
    }

    public DBResponse getDBResponse() {
        return dbResponse;
    }

    public void setDBResponse(DBResponse requestedStats) {
        this.dbResponse = requestedStats.clean();
    }

    public Stack<Move> getMoveStack() {
        return moveStack;
    }

    public void setMoveStack(Stack<Move> moveStack) {
        this.moveStack = moveStack;
    }

    public SyncedItems[] getSyncedLists() {
        return syncedLists;
    }

    public void setSyncedLists(SyncedItems... syncedLists) {

        this.syncedLists = syncedLists;
    }

    public String getRespondingToMsgId() {
        return respondingToMsgId;
    }

    public void setRespondingToMsgId(String respondingToMsgId) {
        this.respondingToMsgId = respondingToMsgId;
    }

    public void setRespondingTo(Message msg) {
        setRespondingToMsgId(msg.messageID);
    }

    public boolean isResponse() {
        return respondingToMsgId != null;
    }

    public String getOtherPlayer() {
        return otherPlayer;
    }

    public void setOtherPlayer(String otherPlayer) {
        this.otherPlayer = otherPlayer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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
        return getMessageType() + " (" + messageID + ") " + StrUtils.strINN(subject, respondingToMsgId, gameTime);
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public boolean isSubject() {
        return subject != null && !subject.trim().equals("");
    }

    public void setHideQuestion(boolean hideQuestion) {
        this.hideQuestion = hideQuestion;
    }

    public boolean hideQuestion() {
        return hideQuestion;
    }
}
