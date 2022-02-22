package ver10.Server.players;

import ver10.SharedClasses.GameSetup.AiParameters;
import ver10.SharedClasses.evaluation.GameStatus;
import ver10.SharedClasses.moves.Move;
import ver10.Model.minimax.Minimax;

import java.util.concurrent.TimeUnit;

public class MyAi extends PlayerAI {
    private Minimax minimax = null;

    public MyAi(AiParameters aiParameters) {
        super(aiParameters);
    }

    @Override
    public void initGame() {
        minimax = new Minimax(game.getModel(), (int) (TimeUnit.MILLISECONDS.toSeconds(moveSearchTimeout.timeInMillis)));
    }

    @Override
    public Move getMove() {
        assert minimax != null;
        return minimax.getBestMove();
    }

    @Override
    public void disconnect(String cause) {
        minimax.end();
    }

    @Override
    public void gameOver(GameStatus gameStatus) {
        super.gameOver(gameStatus);
        minimax.end();
    }

    @Override
    public void cancelRematch() {
        super.cancelRematch();
        minimax.end();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        minimax.end();
    }
}
