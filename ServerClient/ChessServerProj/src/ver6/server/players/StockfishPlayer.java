package ver6.server.players;

import ver6.SharedClasses.GameSetup.AiParameters;
import ver6.SharedClasses.evaluation.GameStatus;
import ver6.SharedClasses.moves.BasicMove;
import ver6.SharedClasses.moves.Move;
import ver6.model_classes.FEN;
import ver6.model_classes.Stockfish;

import java.util.concurrent.TimeUnit;

public class StockfishPlayer extends PlayerAI {
    private final Stockfish stockfish;

    public StockfishPlayer(AiParameters aiParameters) {
        super(aiParameters);
        this.stockfish = new Stockfish();
    }

    @Override
    protected void initGame() {

    }

    @Override
    public Move getMove() {
        String s = stockfish.getBestMove(FEN.generateFEN(game.getModel()), (int) TimeUnit.MILLISECONDS.toSeconds(moveSearchTimeout.timeInMillis));
        BasicMove move = new BasicMove(s);
//        move = new BasicMove(Location.getLoc(move.getMovingFrom().getRow(), Location.getFlipped(move.getMovingFrom().getCol())), Location.getLoc(move.getMovingTo().getRow(), Location.getFlipped(move.getMovingTo().getCol())));
        return game.getModel().findMove(move);
    }

    @Override
    public void disconnect(String cause) {

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
    public void waitForMatch() {

    }
}
