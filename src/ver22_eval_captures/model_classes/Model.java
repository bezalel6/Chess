package ver22_eval_captures.model_classes;

import ver22_eval_captures.Controller;
import ver22_eval_captures.Location;
import ver22_eval_captures.Positions;
import ver22_eval_captures.model_classes.eval_classes.Evaluation;
import ver22_eval_captures.model_classes.eval_classes.Eval;
import ver22_eval_captures.moves.MinimaxMove;
import ver22_eval_captures.moves.Move;
import ver22_eval_captures.types.Piece;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Model {

    private static final int MAX_SCAN_DEPTH = 5;
    private static int ROWS;
    private static int COLS;
    private final Controller controller;
    public Eval eval;
    private Board logicBoard;
    private double scanTime;
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
        board.makeMove(move);
        return move.getAnnotation();
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
        return piece.canMoveTo(board);
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
        MinimaxMove ret = minimax(logicBoard, true, 0, Double.MIN_VALUE, Double.MAX_VALUE, null);
        System.out.println("minimax move = " + ret);
        System.out.println("num of positions reached = " + positionsReached);
        return ret;
    }


    private void initMinimaxTime() {
        minimaxStartedTime = ZonedDateTime.now();
    }

    public MinimaxMove minimax(Board board, boolean isMax, int depth, double a, double b, Move m) {
        positionsReached++;

        Evaluation value = board.getBoardEval();//מחזיר את ההערכה עבור השחקן שתורו לשחק

        if (depth >= MAX_SCAN_DEPTH || value.isGameOver()) {
            return new MinimaxMove(m, value, depth);
        }

        MinimaxMove bestMove = new MinimaxMove(new Evaluation(isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE));

        ArrayList<Move> possibleMoves = board.getAllMoves(isMax ? board.getCurrentPlayer() : board.getOpponent());

        for (Move move : possibleMoves) {
            board.applyMove(move);
            MinimaxMove minimaxMove = minimax(board, !isMax, depth + 1, a, b, depth == 0 ? move : m);
            board.undoMove(move);

            double bestMoveEval = bestMove.getMoveValue().getEval(),
                    minimaxMoveEval = minimaxMove.getMoveValue().getEval();
            if (isMax) {
                if (bestMoveEval < minimaxMoveEval) {
                    bestMove = new MinimaxMove(minimaxMove);
                    a = Math.max(a, minimaxMoveEval);
                } else
                    a = Math.max(a, bestMoveEval);
            } else {
                if (bestMoveEval > minimaxMoveEval) {
                    bestMove = new MinimaxMove(minimaxMove);
                    b = Math.min(b, minimaxMoveEval);
                } else
                    b = Math.min(b, bestMoveEval);
            }
            if (b <= a) {
                break;
            }

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


