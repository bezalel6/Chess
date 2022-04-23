package ver14.SharedClasses.Networking.Messages;

import ver14.SharedClasses.DBActions.DBRequest.DBRequest;
import ver14.SharedClasses.DBActions.DBResponse.DBResponse;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.GameSetup.GameTime;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.Moves.MovesList;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.Misc.IDsGenerator;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/*
 * Message
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * Message -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * Message -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

/**
 * Message.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public class Message implements Serializable {
    private final static IDsGenerator messagesIds = new IDsGenerator();
    /**
     * The Message id.
     */
    public final String messageID;
    private final MessageType messageType;
    private final String subject;
    private String respondingToMsgId;
    private String username = null;
    private String cancelingQuestionCause = null;
    private String otherPlayer = null;
    private Boolean available = null;
    private ArrayList<String> usernameSuggestions = null;
    private PlayerColor playerColor = null;
    private GameTime gameTime = null;
    private LoginInfo loginInfo = null;
    private Move move = null;
    private GameSettings gameSettings = null;
    private MovesList possibleMoves = null;
    private Board board = null;
    private GameStatus gameStatus = null;
    private Question question = null;
    private Question.Answer answer = null;
    private ArrayList<Move> preMoves = null;
    private SyncedItems<?>[] syncedLists = null;
    private Stack<Move> moveStack = null;
    private DBResponse dbResponse = null;
    private DBRequest dbRequest = null;
    private MyError error;

    /**
     * Instantiates a new Message.
     *
     * @param messageType the message type
     */
    public Message(MessageType messageType) {
        this(messageType, null, null);
    }

    /**
     * Instantiates a new Message.
     *
     * @param messageType       the message type
     * @param subject           the subject
     * @param respondingToMsgId the responding to msg id
     */
    public Message(MessageType messageType, String subject, String respondingToMsgId) {
        this.messageType = messageType;
        this.subject = subject;
        this.respondingToMsgId = respondingToMsgId;
        this.messageID = messagesIds.generate();
    }

    /**
     * Instantiates a new Message.
     *
     * @param messageType  the message type
     * @param respondingTo the responding to
     */
    public Message(MessageType messageType, Message respondingTo) {
        this(messageType, null, respondingTo.messageID);
    }

    /**
     * Instantiates a new Message.
     *
     * @param messageType the message type
     * @param subject     the subject
     */
    public Message(MessageType messageType, String subject) {
        this(messageType, subject, null);
    }

    /**
     * Ask for login message.
     *
     * @return the message
     */
    public static Message askForLogin() {
        return new Message(MessageType.LOGIN, "Login");
    }

    /**
     * Return login message.
     *
     * @param loginInfo    the login info
     * @param respondingTo the responding to
     * @return the message
     */
    public static Message returnLogin(LoginInfo loginInfo, Message respondingTo) {
        return new Message(MessageType.LOGIN, respondingTo) {{
            setLoginInfo(loginInfo);
        }};
    }

    public static Message cancelQuestion(Question question, String cause) {
        return new Message(MessageType.CANCEL_QUESTION) {{
            setQuestion(question);
            setCancelingQuestionCause(cause);
        }};
    }

    /**
     * Welcome message message.
     *
     * @param str       the str
     * @param loginInfo the login info
     * @return the message
     */
    public static Message welcomeMessage(String str, LoginInfo loginInfo) {
        return new Message(MessageType.WELCOME_MESSAGE, str) {{
            setLoginInfo(loginInfo);
        }};
    }

    /**
     * Init game message.
     *
     * @param board     the board
     * @param opponent  the opponent
     * @param player    the player
     * @param gameTime  the game time
     * @param moveStack the move stack
     * @return the message
     */
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

    /**
     * Throw error message.
     *
     * @param error if null interrupts
     * @return the message
     */
    public static Message throwError(MyError error) {
        return new Message(MessageType.THROW_ERROR) {{
            setError(error);
        }};
    }

    public static Message askForMove(MovesList possibleMoves, GameTime gameTime) {
        return new Message(MessageType.GET_MOVE, "Its your turn!") {{
            setPossibleMoves(possibleMoves);
            setGameTime(gameTime);
        }};
    }

    /**
     * Update by move message.
     *
     * @param move     the move
     * @param gameTime the game time
     * @return the message
     */
    public static Message updateByMove(Move move, GameTime gameTime) {
        return new Message(MessageType.UPDATE_BY_MOVE) {{
            setMove(move);
            setGameTime(gameTime);
        }};
    }

    /**
     * Wait for your turn message.
     *
     * @param waitingForName the waiting for name
     * @param gameTime       the game time
     * @return the message
     */
    public static Message waitForYourTurn(String waitingForName, GameTime gameTime) {
        waitingForName = StrUtils.dontCapWord(waitingForName);
        return new Message(MessageType.WAIT_TURN, "Wait for " + waitingForName + " to make a move") {{
            setPreMoves(null);
            setGameTime(gameTime);
        }};
    }

    /**
     * Wait for match message.
     *
     * @return the message
     */
    public static Message waitForMatch() {
        return new Message(MessageType.WAIT_FOR_MATCH, "Wait for match");
    }

    /**
     * Game over message.
     *
     * @param gameStatus the game status
     * @return the message
     */
    public static Message gameOver(GameStatus gameStatus) {
        return new Message(MessageType.GAME_OVER, gameStatus.toString()) {{
            setGameStatus(gameStatus);
        }};
    }

    /**
     * Ask for game settings message.
     *
     * @param joinableGames  the joinable games
     * @param resumableGames the resumable games
     * @return the message
     */
    public static Message askForGameSettings(SyncedItems joinableGames, SyncedItems resumableGames) {
        return new Message(MessageType.GET_GAME_SETTINGS) {{
            setSyncedLists(joinableGames, resumableGames);
        }};
    }

    /**
     * Return game settings message.
     *
     * @param gameSettings the game settings
     * @param respondingTo the responding to
     * @return the message
     */
    public static Message returnGameSettings(GameSettings gameSettings, Message respondingTo) {
        return new Message(MessageType.GET_GAME_SETTINGS, respondingTo) {{
            setGameSettings(gameSettings);
        }};
    }

    /**
     * Interrupt message.
     *
     * @return the message
     */
    public static Message interrupt() {
        return new Message(MessageType.INTERRUPT);
    }

    /**
     * Bye message.
     *
     * @param subject the subject
     * @return the message
     */
    public static Message bye(String subject) {
        return new Message(MessageType.BYE, subject);
    }

    /**
     * Error message.
     *
     * @param err the err
     * @return the message
     */
    public static Message error(String err) {
        return new Message(MessageType.ERROR, err);
    }

    /**
     * Return move message.
     *
     * @param move         the move
     * @param respondingTo the responding to
     * @return the message
     */
    public static Message returnMove(Move move, Message respondingTo) {
        return new Message(MessageType.GET_MOVE, respondingTo) {{
            setMove(move);
        }};
    }

    /**
     * Answer question message.
     *
     * @param respondingTo the responding to
     * @return the message
     */
    public static Message answerQuestion(Question.Answer answer, Message respondingTo) {
        return new Message(MessageType.QUESTION, respondingTo) {{
            setAnswer(answer);
        }};
    }

    /**
     * Ask question message.
     *
     * @param question the question
     * @return the message
     */
    public static Message askQuestion(Question question) {
        return new Message(MessageType.QUESTION) {{
            setQuestion(question);
        }};
    }

    /**
     * Check username availability message.
     *
     * @param username the username
     * @return the message
     */
    public static Message checkUsernameAvailability(String username) {
        return new Message(MessageType.USERNAME_AVAILABILITY) {{
            setUsername(username);
        }};
    }

    /**
     * Return username not available message.
     *
     * @param usernameSuggestions the username suggestions
     * @param request             the request
     * @return the message
     */
    public static Message returnUsernameNotAvailable(ArrayList<String> usernameSuggestions, Message request) {
        return new Message(MessageType.USERNAME_AVAILABILITY, request) {{
            setUsernameSuggestions(usernameSuggestions);
            setAvailable(false);
        }};
    }

    /**
     * Return username available message.
     *
     * @param request the request
     * @return the message
     */
    public static Message returnUsernameAvailable(Message request) {
        return new Message(MessageType.USERNAME_AVAILABILITY, request) {{
            setAvailable(true);
        }};
    }

    /**
     * Db request message.
     *
     * @param dbRequest the db request
     * @return the message
     */
    public static Message dbRequest(DBRequest dbRequest) {
        return new Message(MessageType.DB_REQUEST) {{
            setDbRequest(dbRequest);
        }};
    }

    /**
     * Sets db request.
     *
     * @param dbRequest the db request
     */
    public void setDbRequest(DBRequest dbRequest) {
        this.dbRequest = dbRequest;
    }

    /**
     * Return db response message.
     *
     * @param DBResponse the db response
     * @param request    the request
     * @return the message
     */
    public static Message returnDBResponse(DBResponse DBResponse, Message request) {
        return new Message(MessageType.DB_RESPONSE, request) {{
            setDBResponse(DBResponse);
        }};
    }

    /**
     * Sync lists message.
     *
     * @param syncedLists the synced lists
     * @return the message
     */
    public static Message syncLists(SyncedItems<?>... syncedLists) {
        return new Message(MessageType.UPDATE_SYNCED_LIST) {{
            setSyncedLists(syncedLists);
        }};
    }

    public String getCancelingQuestionCause() {
        return cancelingQuestionCause;
    }

    public void setCancelingQuestionCause(String cancelingQuestionCause) {
        this.cancelingQuestionCause = cancelingQuestionCause;
    }

    public Question.Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Question.Answer answer) {
        this.answer = answer;
    }

    /**
     * Gets error.
     *
     * @return the error
     */
    public MyError getError() {
        return error;
    }

    /**
     * Sets error.
     *
     * @param error the error
     */
    public void setError(MyError error) {
        this.error = error;
    }

    /**
     * Gets db request.
     *
     * @return the db request
     */
    public DBRequest getDBRequest() {
        return dbRequest;
    }

    /**
     * Gets username suggestions.
     *
     * @return the username suggestions
     */
    public ArrayList<String> getUsernameSuggestions() {
        return usernameSuggestions;
    }

    /**
     * Sets username suggestions.
     *
     * @param usernameSuggestions the username suggestions
     */
    public void setUsernameSuggestions(ArrayList<String> usernameSuggestions) {
        this.usernameSuggestions = usernameSuggestions;
    }

    /**
     * Gets db response.
     *
     * @return the db response
     */
    public DBResponse getDBResponse() {
        return dbResponse;
    }

    /**
     * Sets db response.
     *
     * @param requestedStats the requested stats
     */
    public void setDBResponse(DBResponse requestedStats) {
        this.dbResponse = requestedStats.clean();
    }

    /**
     * Gets move stack.
     *
     * @return the move stack
     */
    public Stack<Move> getMoveStack() {
        return moveStack;
    }

    /**
     * Sets move stack.
     *
     * @param moveStack the move stack
     */
    public void setMoveStack(Stack<Move> moveStack) {
        this.moveStack = moveStack;
    }

    /**
     * Get synced lists synced items [ ].
     *
     * @return the synced items [ ]
     */
    public SyncedItems<?>[] getSyncedLists() {
        return syncedLists;
    }

    /**
     * Sets synced lists.
     *
     * @param syncedLists the synced lists
     */
    public void setSyncedLists(SyncedItems<?>... syncedLists) {

        this.syncedLists = syncedLists;
    }

    /**
     * Gets responding to msg id.
     *
     * @return the responding to msg id
     */
    public String getRespondingToMsgId() {
        return respondingToMsgId;
    }

    /**
     * Sets responding to msg id.
     *
     * @param respondingToMsgId the responding to msg id
     */
    public void setRespondingToMsgId(String respondingToMsgId) {
        this.respondingToMsgId = respondingToMsgId;
    }

    /**
     * Sets responding to.
     *
     * @param msg the msg
     */
    public void setRespondingTo(Message msg) {
        setRespondingToMsgId(msg.messageID);
    }

    /**
     * Is response boolean.
     *
     * @return the boolean
     */
    public boolean isResponse() {
        return respondingToMsgId != null;
    }

    /**
     * Gets other player.
     *
     * @return the other player
     */
    public String getOtherPlayer() {
        return otherPlayer;
    }

    /**
     * Sets other player.
     *
     * @param otherPlayer the other player
     */
    public void setOtherPlayer(String otherPlayer) {
        this.otherPlayer = otherPlayer;
    }

    /**
     * Gets question.
     *
     * @return the question
     */
    public Question getQuestion() {
        return question;
    }

    /**
     * Sets question.
     *
     * @param question the question
     */
    public void setQuestion(Question question) {
        this.question = question;
    }

    /**
     * Gets available.
     *
     * @return the available
     */
    public Boolean getAvailable() {
        return available;
    }

    /**
     * Sets available.
     *
     * @param available the available
     */
    public void setAvailable(Boolean available) {
        this.available = available;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets game settings.
     *
     * @return the game settings
     */
    public GameSettings getGameSettings() {
        return gameSettings;
    }

    /**
     * Sets game settings.
     *
     * @param gameSettings the game settings
     */
    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    /**
     * Gets pre moves.
     *
     * @return the pre moves
     */
    public ArrayList<Move> getPreMoves() {
        return preMoves;
    }

    /**
     * Sets pre moves.
     *
     * @param preMoves the pre moves
     */
    public void setPreMoves(ArrayList<Move> preMoves) {
        this.preMoves = preMoves;
    }

    /**
     * Gets subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets player color.
     *
     * @return the player color
     */
    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    /**
     * Sets player color.
     *
     * @param playerColor the player color
     */
    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    /**
     * Gets move.
     *
     * @return the move
     */
    public Move getMove() {
        return move;
    }

    /**
     * Sets move.
     *
     * @param move the move
     */
    public void setMove(Move move) {
        this.move = move;
    }

    /**
     * Gets game time.
     *
     * @return the game time
     */
    public GameTime getGameTime() {
        return gameTime;
    }

    /**
     * Sets game time.
     *
     * @param gameTime the game time
     */
    public void setGameTime(GameTime gameTime) {
        this.gameTime = gameTime;
    }

    /**
     * Gets game status.
     *
     * @return the game status
     */
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    /**
     * Sets game status.
     *
     * @param gameStatus the game status
     */
    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    /**
     * Gets possible moves.
     *
     * @return the possible moves
     */
    public MovesList getPossibleMoves() {
        return possibleMoves;
    }

    /**
     * Sets possible moves.
     *
     * @param possibleMoves the possible moves
     */
    public void setPossibleMoves(MovesList possibleMoves) {
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

    /**
     * Gets board.
     *
     * @return the board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sets board.
     *
     * @param board the board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Gets login info.
     *
     * @return the login info
     */
    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    /**
     * Sets login info.
     *
     * @param loginInfo the login info
     */
    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    @Override
    public String toString() {
        return getMessageType() + " (" + messageID + ") " + StrUtils.strINN(subject, respondingToMsgId);
    }

    /**
     * Gets message type.
     *
     * @return the message type
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * Is subject boolean.
     *
     * @return the boolean
     */
    public boolean isSubject() {
        return !StrUtils.isEmpty(subject);
    }


}
