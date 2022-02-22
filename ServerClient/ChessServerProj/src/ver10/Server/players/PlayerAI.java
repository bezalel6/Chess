package ver10.Server.players;

import ver10.SharedClasses.Callbacks.Callback;
import ver10.SharedClasses.GameSettings;
import ver10.SharedClasses.GameSetup.AiParameters;
import ver10.SharedClasses.Question;
import ver10.SharedClasses.Sync.SyncedItems;
import ver10.SharedClasses.TimeFormat;
import ver10.SharedClasses.evaluation.GameStatus;
import ver10.SharedClasses.moves.Move;

/**
 * PlayerAI.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public abstract class PlayerAI extends Player {
    final TimeFormat moveSearchTimeout;

    public PlayerAI(AiParameters aiParameters) {
        super(aiParameters.getAiType().toString());
        this.moveSearchTimeout = aiParameters.getMoveSearchTimeout();
    }

    public static PlayerAI createPlayerAi(AiParameters aiParameters) {
        return switch (aiParameters.getAiType()) {
            case MyAi -> new MyAi(aiParameters);
            case Stockfish -> new StockfishPlayer(aiParameters);
        };
    }

    @Override
    public void waitTurn() {
    }

    @Override
    public void gameOver(GameStatus gameStatus) {
    }

    @Override
    public boolean askForRematch() {
        return true;
    }

    @Override
    public void updateByMove(Move move) {

    }

    @Override
    public void cancelRematch() {

    }

    @Override
    public void interrupt() {

    }

    @Override
    public void waitForMatch() {

    }

    @Override
    public void drawOffered(Callback<Question.Answer> answerCallback) {
        answerCallback.callback(Question.Answer.NO);
    }

    @Override
    public GameSettings getGameSettings(SyncedItems joinableGames, SyncedItems resumableGames) {
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
