package ver12.players;

import ver12.Model.FEN;
import ver12.Model.Stockfish;
import ver12.SharedClasses.GameSetup.AiParameters;
import ver12.SharedClasses.evaluation.GameStatus;
import ver12.SharedClasses.moves.BasicMove;
import ver12.SharedClasses.moves.Move;

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
        String s = stockfish.getBestMove(FEN.generateFEN(game.getModel()), 1);
        BasicMove move = new BasicMove(s);
//        move = new BasicMove(Location.getLoc(move.getMovingFrom().row, Location.getFlipped(move.getMovingFrom().col)), Location.getLoc(move.getMovingTo().row, Location.getFlipped(move.getMovingTo().col)));
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
