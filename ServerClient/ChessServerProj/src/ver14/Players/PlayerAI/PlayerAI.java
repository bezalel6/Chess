package ver14.Players.PlayerAI;

import ver14.Game.Game;
import ver14.Players.Player;
import ver14.Players.PlayerAI.Stockfish.StockfishPlayer;
import ver14.SharedClasses.Callbacks.AnswerCallback;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.AISettings;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;

import java.util.HashMap;
import java.util.Map;


/**
 * represents a player choosing moves using Artificial Intelligence .
 */
public abstract class PlayerAI extends Player {
    private final static long safetyNet = 500;
    private final AISettings AISettings;
    protected Map<Question.QuestionType, Question.Answer> qNa = new HashMap<>();
    long moveSearchTimeout;

    public PlayerAI(AISettings AISettings) {
        super(AISettings.getAiType().toString());
        this.AISettings = AISettings;
        setAnswer(Question.QuestionType.REMATCH, Question.Answer.YES);
        setAnswer(Question.QuestionType.DRAW_OFFER, Question.Answer.DO_NOT_ACCEPT);
    }

    protected void setAnswer(Question.QuestionType questionType, Question.Answer answer) {
        qNa.put(questionType, answer);
    }

    public static PlayerAI createPlayerAi(AISettings AISettings) {
        return switch (AISettings.getAiType()) {
            case MyAi -> new MyAi(AISettings);
            case Stockfish -> new StockfishPlayer(AISettings);
        };
    }

    @Override
    public void initGame(Game game) {
        long time = Math.min(game.getGameTime().getTimeFormat(getPlayerColor()).timeInMillis, AISettings.getMoveSearchTimeout().timeInMillis);
        time -= safetyNet;
        time = Math.max(0, time);
        this.moveSearchTimeout = time;
        super.initGame(game);
    }

    @Override
    public void error(String error) {
        interrupt(new MyError(error));
    }

    @Override
    public void waitTurn() {
    }

    @Override
    public void gameOver(GameStatus gameStatus) {
    }

    @Override
    public void askQuestion(Question question, AnswerCallback onAns) {
        if (qNa.containsKey(question.questionType)) {
            onAns.callback(qNa.get(question.questionType));
        } else {
            super.askQuestion(question, onAns);
        }
    }

    @Override
    public void updateByMove(Move move) {

    }

    @Override
    public void cancelQuestion(Question question, String cause) {

    }

    @Override
    public void interrupt(MyError error) {

    }

    @Override
    public void waitForMatch() {

    }

    @Override
    public GameSettings getGameSettings(SyncedItems<?> joinableGames, SyncedItems<?> resumableGames) {
        return null;
    }

    @Override
    public boolean isGuest() {
        return false;
    }

    @Override
    public boolean isAi() {
        return true;
    }
}
