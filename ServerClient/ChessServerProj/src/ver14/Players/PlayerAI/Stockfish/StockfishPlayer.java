package ver14.Players.PlayerAI.Stockfish;

import ver14.Model.FEN;
import ver14.Players.PlayerAI.PlayerAI;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.AISettings;
import ver14.SharedClasses.Game.Moves.BasicMove;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Misc.Question;


/**
 * represents a Stockfish Player, that always plays the best moves.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class StockfishPlayer extends PlayerAI {
    /**
     * The Stockfish.
     */
    private final Stockfish stockfish;

    /**
     * Instantiates a new Stockfish player.
     *
     * @param AISettings the ai parameters
     */
    public StockfishPlayer(AISettings AISettings) {
        super(AISettings);
        this.stockfish = new Stockfish();
    }

    /**
     * Init game.
     */
    @Override
    protected void initGame() {
    }

    /**
     * Gets move.
     *
     * @return the move
     */
    @Override
    public Move getMove() {
        String s = stockfish.getBestMove(FEN.generateFEN(game.getModel()), 100);
        BasicMove move = new BasicMove(s);
//        move = new BasicMove(Location.getLoc(move.getsource().row, Location.getFlipped(move.getsource().col)), Location.getLoc(move.getdestination().row, Location.getFlipped(move.getdestination().col)));
        return game.getModel().findMove(move);
    }

    /**
     * Disconnect.
     *
     * @param cause             the cause
     * @param notifyGameSession the notify game session
     */
    @Override
    public void disconnect(String cause, boolean notifyGameSession) {
        stockfish.stopEngine();
    }

    /**
     * Wait turn.
     */
    @Override
    public void waitTurn() {

    }

    /**
     * Game over.
     *
     * @param gameStatus the game status
     */
    @Override
    public void gameOver(GameStatus gameStatus) {

    }

    /**
     * Update by move.
     *
     * @param move the move
     */
    @Override
    public void updateByMove(Move move) {

    }

    /**
     * Cancel question.
     *
     * @param question the question
     * @param cause    the cause
     */
    @Override
    public void cancelQuestion(Question question, String cause) {

    }

    /**
     * Wait for match.
     */
    @Override
    public void waitForMatch() {

    }
}
