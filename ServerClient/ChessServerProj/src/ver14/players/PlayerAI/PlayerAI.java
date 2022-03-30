package ver14.players.PlayerAI;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.SharedClasses.Game.TimeFormat;
import ver14.SharedClasses.Game.evaluation.GameStatus;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Question;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.players.Player;

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
    public void error(String error) {
        interrupt();
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
