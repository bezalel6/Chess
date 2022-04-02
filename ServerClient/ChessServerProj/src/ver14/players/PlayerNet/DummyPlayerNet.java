package ver14.players.PlayerNet;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.SharedClasses.Game.evaluation.GameStatus;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Question;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.players.Player;

public class DummyPlayerNet extends Player {
    public DummyPlayerNet() {
        super("dumbdumb");
    }

    @Override
    protected void initGame() {

    }

    @Override
    public void error(String error) {

    }

    @Override
    public Move getMove() {
        return game.getModel().generateAllMoves().get(0);
    }

    @Override
    public void waitTurn() {

    }

    @Override
    public void gameOver(GameStatus gameStatus) {

    }

    @Override
    public boolean askForRematch() {
        return false;
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
    public void disconnect(String cause) {

    }

    @Override
    public void waitForMatch() {

    }

    @Override
    public void drawOffered(Callback<Question.Answer> answerCallback) {

    }

    @Override
    public GameSettings getGameSettings(SyncedItems<?> joinableGames, SyncedItems<?> resumableGames) {
        return new GameSettings(AiParameters.EZ_MY_AI);
    }

    @Override
    public boolean isGuest() {
        return false;
    }

    @Override
    public boolean isAi() {
        return false;
    }
}
