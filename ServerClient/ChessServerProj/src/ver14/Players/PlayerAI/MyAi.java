package ver14.Players.PlayerAI;

import ver14.Model.Minimax.Minimax;
import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;

/*
 * MyAi
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * MyAi -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * MyAi -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

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
    public void disconnect(String cause, boolean notifyGameSession) {
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
