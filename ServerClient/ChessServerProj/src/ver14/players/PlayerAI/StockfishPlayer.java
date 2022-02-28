package ver14.players.PlayerAI;

import ver14.Model.FEN;
import ver14.Model.Stockfish;
import ver14.SharedClasses.GameSetup.AiParameters;
import ver14.SharedClasses.evaluation.GameStatus;
import ver14.SharedClasses.moves.BasicMove;
import ver14.SharedClasses.moves.Move;

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
        String s = stockfish.getBestMove(FEN.generateFEN(game.getModel()), 100);
        BasicMove move = new BasicMove(s);
//        move = new BasicMove(Location.getLoc(move.getMovingFrom().row, Location.getFlipped(move.getMovingFrom().col)), Location.getLoc(move.getMovingTo().row, Location.getFlipped(move.getMovingTo().col)));
        return game.getModel().findMove(move);
    }

    @Override
    public void disconnect(String cause) {
        stockfish.stopEngine();
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
