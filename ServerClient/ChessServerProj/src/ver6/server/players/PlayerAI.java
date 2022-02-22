package ver6.server.players;

import ver6.SharedClasses.GameSettings;
import ver6.SharedClasses.GameSetup.AiParameters;
import ver6.SharedClasses.Sync.SyncedItems;
import ver6.SharedClasses.TimeFormat;
import ver6.SharedClasses.evaluation.GameStatus;
import ver6.SharedClasses.moves.Move;

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
    public GameSettings getGameSettings(SyncedItems joinableGames, SyncedItems resumableGames) {
        return null;
    }
}
