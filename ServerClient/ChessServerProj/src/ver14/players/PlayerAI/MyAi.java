package ver14.players.PlayerAI;

import ver14.Model.minimax.Minimax;
import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Question;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;

public class MyAi extends PlayerAI {
    private Minimax minimax = null;

    public MyAi(AiParameters aiParameters) {
        super(aiParameters);
    }

    @Override
    public void initGame() {

        minimax = new Minimax(game.getModel(), (int) moveSearchTimeout, 0);
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
    public void cancelQuestion(Question question, String cause) {
        super.cancelQuestion(question, cause);
        minimax.end();
    }

    @Override
    public void interrupt(MyError error) {
        super.interrupt(error);
        minimax.interrupt(error);
    }
}
