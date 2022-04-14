package ver14.players.PlayerAI;

import ver14.SharedClasses.Callbacks.QuestionCallback;
import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.SharedClasses.Game.evaluation.GameStatus;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Question;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.game.Game;
import ver14.players.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * PlayerAI.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public abstract class PlayerAI extends Player {
    private final static long safetyNet = 500;
    private final AiParameters aiParameters;
    protected Map<Question, Question.Answer> qNa = new HashMap<>();
    long moveSearchTimeout;

    public PlayerAI(AiParameters aiParameters) {
        super(aiParameters.getAiType().toString());
        this.aiParameters = aiParameters;

    }

    public static PlayerAI createPlayerAi(AiParameters aiParameters) {
        return switch (aiParameters.getAiType()) {
            case MyAi -> new MyAi(aiParameters);
            case Stockfish -> new StockfishPlayer(aiParameters);
        };
    }

    @Override
    public void initGame(Game game) {
        long time = Math.min(game.getGameTime().getTimeFormat(getPlayerColor()).timeInMillis, aiParameters.getMoveSearchTimeout().timeInMillis);
        time -= safetyNet;
        time = Math.max(0, time);
        this.moveSearchTimeout = time;
        super.initGame(game);
    }

    @Override
    public void error(String error) {
        interrupt(new MyError(error, ErrorType.UnKnown));
    }

    @Override
    public void waitTurn() {
    }

    @Override
    public void gameOver(GameStatus gameStatus) {
    }

    @Override
    public void askQuestion(Question question, QuestionCallback onAns) {
        if (qNa.containsKey(question)) {
            onAns.callback(qNa.get(question));
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
    public void drawOffered(QuestionCallback answerCallback) {
        answerCallback.callback(Question.Answer.NO);
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

    protected void setAnswer(Question question, Question.Answer answer) {
        qNa.put(question, answer);
    }
}
