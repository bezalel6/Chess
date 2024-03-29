package ver21_square_class.model_classes;

import ver21_square_class.Controller;
import ver21_square_class.Location;
import ver21_square_class.Positions;
import ver21_square_class.model_classes.eval_classes.BoardEval;
import ver21_square_class.model_classes.eval_classes.Eval;
import ver21_square_class.moves.MinimaxMove;
import ver21_square_class.moves.Move;
import ver21_square_class.types.Piece;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Model {

    private static final int MAX_SCAN_DEPTH = 10;
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

    private double getElapsedTime() {
        return ((minimaxStartedTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS)) / 1000);
    }

    private void initMinimaxTime() {
        minimaxStartedTime = ZonedDateTime.now();
    }

    public MinimaxMove minimax(Board board, boolean isMax, int depth, double a, double b, Move m) {
        positionsReached++;
        MinimaxMove bestMove = new MinimaxMove(new BoardEval(isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE));

        BoardEval value = board.getBoardEval();//מחזיר את ההערכה עבור השחקן שתורו לשחק

        double elapsedTime = getElapsedTime();

        if (elapsedTime >= scanTime || depth >= MAX_SCAN_DEPTH || value.isGameOver()) {
            return new MinimaxMove(m, value, depth);
        }

        ArrayList<Move> possibleMoves = board.getAllMoves(isMax ? board.getCurrentPlayer() : board.getOpponent());

        if (depth == 0 && possibleMoves.size() > 0) {
            initMinimaxTime();
//            scanTime /= possibleMoves.size();
//            scanTime /= 1000;
        }

        for (int i = 0, possibleMovesSize = possibleMoves.size(); i < possibleMovesSize && getElapsedTime() <= scanTime; i++) {
            Move move = possibleMoves.get(i);
            if (depth == 0) {
                m = move;
            }
            board.applyMove(move);
            MinimaxMove minimaxMove = minimax(board, !isMax, depth + 1, a, b, m);
            board.undoMove(move);

            double bestMoveEval = bestMove.getMoveValue().getEval(),
                    minimaxMoveValue = minimaxMove.getMoveValue().getEval();
            double bestMoveDepth = bestMove.getMoveDepth(),
                    minimaxMoveDepth = minimaxMove.getMoveDepth();
            if (isMax) {
                if (bestMoveEval < minimaxMoveValue
                        || (bestMoveEval == minimaxMoveValue && bestMoveDepth > minimaxMoveDepth)) {
                    bestMove = new MinimaxMove(minimaxMove);
                    a = Math.max(a, minimaxMoveValue);
                } else
                    a = Math.max(a, bestMoveEval);
            } else {
                if (bestMoveEval > minimaxMoveValue
                        || (bestMoveEval == minimaxMoveValue && bestMoveDepth < minimaxMoveDepth)) {
                    bestMove = new MinimaxMove(minimaxMove);
                    b = Math.min(b, minimaxMoveValue);
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


