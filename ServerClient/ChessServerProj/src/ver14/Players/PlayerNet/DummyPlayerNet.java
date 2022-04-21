package ver14.Players.PlayerNet;

import ver14.Players.Player;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;

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
    public void updateByMove(Move move) {

    }

    @Override
    public void cancelQuestion(Question question, String cause) {

    }

    @Override
    public void interrupt(MyError error) {

    }

    @Override
    public void disconnect(String cause) {

    }

    @Override
    public void waitForMatch() {

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
