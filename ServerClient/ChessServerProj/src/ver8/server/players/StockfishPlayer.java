package ver8.server.players;

import ver8.SharedClasses.GameSetup.AiParameters;
import ver8.SharedClasses.evaluation.GameStatus;
import ver8.SharedClasses.moves.BasicMove;
import ver8.SharedClasses.moves.Move;
import ver8.model_classes.FEN;
import ver8.model_classes.Stockfish;

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
