package ver14.players.PlayerAI;

import ver14.Model.FEN;
import ver14.Model.Stockfish;
import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.SharedClasses.Game.evaluation.GameStatus;
import ver14.SharedClasses.Game.moves.BasicMove;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Question;

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
    public void updateByMove(Move move) {

    }

    @Override
    public void cancelQuestion(Question question, String cause) {

    }

    @Override
    public void waitForMatch() {

    }
}