package ver3.model_classes;

import Global_Classes.Positions;
import ver3.Game;
import ver3.model_classes.moves.Move;
import ver3.model_classes.pieces.Piece;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Model {

    private final Game game;
    private Board logicBoard;

    public Model(Game game) {
        this.game = game;
    }

    public void initGame(int startingPosition) {
        initGame(Positions.getAllPositions().get(startingPosition).getFen());
    }

    public void initGame(String fen) {
        logicBoard = new Board(fen);
    }

    public String makeMove(Move move, Board board) {
        board.makeMove(move);
        return move.getAnnotation();
    }


    public void unmakeMove(Move move, Board board) {
        board.unmakeMove();
    }

    public Piece getPiece(Location loc) {
        return logicBoard.getPiece(loc);
    }

    public ArrayList<Move> getMovesFrom(Location movingFrom) {
        return logicBoard.generateAllMoves().stream()
                .filter(m -> m.getMovingFrom().equals(movingFrom))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Board getBoard() {
        return logicBoard;
    }

    public void setBoard(Board board) {
        this.logicBoard = board;
    }

    public Board getLogicBoard() {
        return logicBoard;
    }


}


