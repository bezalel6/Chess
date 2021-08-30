package ver19_square_control;

import ver19_square_control.types.Piece.Player;
import ver19_square_control.moves.MinimaxMove;
import ver19_square_control.moves.Move;
import ver19_square_control.types.Piece;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Model {

    private static final int MAX_SCAN_DEPTH = 50;
    private static int ROWS;
    private static int COLS;
    public Eval eval;
    private Board logicBoard;
    private Controller controller;
    private int scanTime;
    private ZonedDateTime minimaxStartedTime;
    private long positionsReached;

    public Model(int boardSize, Controller controller) {
        this.controller = controller;
        ROWS = COLS = boardSize;
    }

    public void initGame(int startingPosition) {
        logicBoard = new Board(Positions.getAllPositions().get(startingPosition).getFen(), this);
        eval = new Eval(logicBoard);
    }

    public String makeMove(Move move, Board board) {
//        String moveAnnotation = move.getAnnotation();
        board.makeMove(move);
        return move.toString();
    }

    public void unmakeMove(Move move, Board board) {
        board.unmakeMove(move);
    }

    public ArrayList<Location> getPiecesLocations(int currentPlayer) {
        ArrayList<Location> ret = new ArrayList<>();
        for (Piece piece : getBoard().getPlayersPieces(currentPlayer))
            if (!piece.isCaptured())
                ret.add(piece.getLoc());
        return ret;
    }

    public boolean isInBounds(Location loc) {
        return loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS;
    }

    public Piece getPiece(Location loc, Board board) {
        if (isInBounds(loc))
            return board.getPiece(loc);
        return null;
    }

    public ArrayList<Move> getMoves(Piece piece, Board board) {
        if (piece == null) {
            return null;
        }
        ArrayList<Move> list = piece.canMoveTo(board);
        return list;
    }

    public Board getBoard() {
        return logicBoard;
    }

    public void setBoard(Board board) {
        this.logicBoard = board;
    }

    // מהלך מינימקס
    public Move getAiMove() {
        System.out.println("Model getAiMove() using MINIMAX");
        scanTime = controller.getScanTime();
        return getBestMoveUsingMinimax().getMove();
    }

    // ============================ for minimax ===========================
    private MinimaxMove getBestMoveUsingMinimax() {
        minimaxStartedTime = ZonedDateTime.now();
        positionsReached = 0;
        MinimaxMove ret = minimax(logicBoard, true, 0, scanTime, Double.MIN_VALUE, Double.MAX_VALUE);
        System.out.println("minimax move = " + ret);
        System.out.println("num of positions reached = " + positionsReached);
        return ret;
    }

    private long getElapsedTime() {
        return minimaxStartedTime.until(ZonedDateTime.now(), ChronoUnit.SECONDS);
    }

    public MinimaxMove minimax(Board board, boolean isMax, int depth, int maxTime, double a, double b) {
        positionsReached++;
        MinimaxMove bestMove = new MinimaxMove(null, new BoardEval(isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE), -1);

        BoardEval value = board.getBoardEval();

        long seconds = getElapsedTime();

        if (seconds >= maxTime || depth >= MAX_SCAN_DEPTH || value.isGameOver()) {
            return new MinimaxMove(null, value, depth);
        }

        int player = isMax ? board.getCurrentPlayer() : board.getOpponent();
        ArrayList<Move> possibleMoves = board.getAllMoves(player);

        //todo time management: divide the time between the branches
        int bTime = maxTime;

        for (int i = 0; i < possibleMoves.size(); i++) {
            Move move = possibleMoves.get(i);
            board.applyMove(move);

            MinimaxMove minimaxMove = minimax(board, !isMax, depth + 1, bTime, a, b);
            if (depth == 0)
                minimaxMove.setMove(move);

            board.undoMove(move);

            double bestMoveEval = bestMove.getMoveValue().getEval(), minimaxMoveValue = minimaxMove.getMoveValue().getEval();
            if (isMax) {
                if (bestMoveEval < minimaxMoveValue) {
                    bestMove = minimaxMove;
                }
                a = Math.max(a, bestMove.getMoveValue().getEval());
            } else {
                if (bestMoveEval > minimaxMoveValue) {
                    bestMove = minimaxMove;
                }
                b = Math.min(b, bestMove.getMoveValue().getEval());
            }
            if (b <= a) break;

        }
        return bestMove;
    }

    public void printAllPossibleMoves() {
        for (Move move : logicBoard.getAllMovesForCurrentPlayer()) {
            System.out.println(move);
        }
    }

    public Controller getController() {
        return controller;
    }
}


