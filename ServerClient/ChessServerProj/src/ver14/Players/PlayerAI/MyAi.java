package ver14.Players.PlayerAI;

import ver14.Model.Minimax.Minimax;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.AISettings;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;


/**
 * My ai - represents an ai player using the {@link Minimax} algorithm to choose moves.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MyAi extends PlayerAI {
    /**
     * The Minimax.
     */
    private Minimax minimax = null;

    /**
     * Instantiates a new My ai.
     *
     * @param AISettings the ai parameters
     */
    public MyAi(AISettings AISettings) {
        super(AISettings);
    }

    /**
     * Init game.
     */
    @Override
    public void initGame() {

        minimax = new Minimax(game.getModel(), (int) moveSearchTimeout, 0);
    }

    /**
     * Gets move.
     *
     * @return the move
     */
    @Override
    public Move getMove() {
        assert minimax != null;
        return minimax.getBestMove(getPlayerColor());
    }

    /**
     * Disconnect.
     *
     * @param cause             the cause
     * @param notifyGameSession the notify game session
     */
    @Override
    public void disconnect(String cause, boolean notifyGameSession) {
        minimax.end();
    }

    /**
     * Game over.
     *
     * @param gameStatus the game status
     */
    @Override
    public void gameOver(GameStatus gameStatus) {
        super.gameOver(gameStatus);
        minimax.end();

    }

    /**
     * Cancel question.
     *
     * @param question the question
     * @param cause    the cause
     */
    @Override
    public void cancelQuestion(Question question, String cause) {
        super.cancelQuestion(question, cause);
        minimax.end();
    }

    /**
     * Interrupt.
     *
     * @param error the error
     */
    @Override
    public void interrupt(MyError error) {
        super.interrupt(error);
        minimax.interrupt(error);
    }
}
