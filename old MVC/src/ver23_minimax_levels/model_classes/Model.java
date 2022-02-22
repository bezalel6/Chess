package ver23_minimax_levels.model_classes;

import ver23_minimax_levels.Controller;
import ver23_minimax_levels.Location;
import ver23_minimax_levels.MyError;
import ver23_minimax_levels.Positions;
import ver23_minimax_levels.model_classes.eval_classes.Eval;
import ver23_minimax_levels.model_classes.eval_classes.Evaluation;
import ver23_minimax_levels.model_classes.eval_classes.ExtendedEvaluation;
import ver23_minimax_levels.moves.MinimaxMove;
import ver23_minimax_levels.moves.Move;
import ver23_minimax_levels.types.Piece;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Model {

    private static int ROWS;
    private static int COLS;
    private final Controller controller;
    public Eval eval;
    private Board logicBoard;

    private MinimaxMove bestMoveSoFar;
    private ArrayList<MinimaxMove> movesFound;
    private ZonedDateTime minimaxStartedTime;
    private long positionsReached;
    private long leavesReached;

    public Model(int boardSize, Controller controller) {
        this.controller = controller;
        ROWS = COLS = boardSize;
    }

    public void initGame(int startingPosition) {
        logicBoard = new Board(Positions.getAllPositions().get(startingPosition).getFen());
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

    public void setLogicBoard(Board logicBoard) {
        this.logicBoard = logicBoard;
    }

    public Board getBoard() {
        return logicBoard;
    }

    public void setBoard(Board board) {
        this.logicBoard = board;
    }

    private void initMinimaxTime() {
        minimaxStartedTime = ZonedDateTime.now();
    }

    private long getElapsedSeconds() {
        return minimaxStartedTime.until(ZonedDateTime.now(), ChronoUnit.SECONDS);
    }

    private double[] alphaBetaInit() {
        return new double[]{Double.MIN_VALUE, Double.MAX_VALUE};
    }

    // מהלך מינימקס
    public Move getAiMove() {
        System.out.println("Model getAiMove() using MINIMAX");
        initMinimaxTime();
        return getBestMoveUsingMinimax().getMove();
    }

    // ============================ for minimax ===========================
    private MinimaxMove getBestMoveUsingMinimax() {
        System.out.println("Current eval:\n" + logicBoard.getBoardEval() + Controller.HIDE_PRINT);
        bestMoveSoFar = null;
        positionsReached = -1;
        leavesReached = 0;
        movesFound = new ArrayList<>();
        int i = 1;
        while (getElapsedSeconds() < controller.getScanTime()) {
            positionsReached = -1;
            leavesReached = 0;

            String s = "-------------";
            System.out.println("\n" + s + "Starting search on depth " + i + s);
//            todo remember to update the logicBoard whenever theres a new board i want to start using
            MinimaxMove minimaxMove = minimax(i);
            System.out.println("depth " + i + " move: " + minimaxMove);
            i++;
            if (minimaxMove != null && (bestMoveSoFar == null || minimaxMove.isCompleteSearch())) {
                bestMoveSoFar = minimaxMove;
            }
            controller.clearAllDrawings();
            controller.drawMove(bestMoveSoFar.getMove(), Controller.MINIMAX_BEST_MOVE);

        }
        System.out.println("\nminimax move: " + bestMoveSoFar);
        System.out.println("max depth reached: " + i);
        System.out.println("num of positions reached: " + positionsReached);
        System.out.println("num of leaves reached: " + leavesReached);
        return bestMoveSoFar;
    }


    public MinimaxMove minimax(int maxDepth) {
        return minimax(logicBoard, true, 0, maxDepth, -1, alphaBetaInit(), null);
    }

    public MinimaxMove minimax(Board board, boolean isMax, int depth, int maxDepth, int moveIndex, double[] alphaBeta, Move m) {
        positionsReached++;

        Evaluation value = board.getBoardEval();//מחזיר את ההערכה עבור השחקן שתורו לשחק

        if (depth >= maxDepth || value.isGameOver()) {
            leavesReached++;
            MinimaxMove ret = new MinimaxMove(m, value, depth, moveIndex, board, alphaBeta);
//            movesFound.add(new MinimaxMove(ret));
            return ret;
        }
        MinimaxMove bestMove;
        ArrayList<Move> possibleMoves;
        int movingPlayer;
        if (isMax) {
            bestMove = new MinimaxMove(new Evaluation(Integer.MIN_VALUE));
            movingPlayer = board.getCurrentPlayer();
        } else {
            bestMove = new MinimaxMove(new Evaluation(Integer.MAX_VALUE));
            movingPlayer = board.getOpponent();
        }
        boolean isRoot = depth == 0, isUsingSavedMoves = false;
//        if (isRoot && !movesFound.isEmpty()) {
////            todo important: use the alphabeta from the first saved move b4 sorting!
//            alphaBeta = movesFound.get(0).getAlphaBeta().clone();
//            Collections.sort(movesFound);
//            Collections.reverse(movesFound);
//            isUsingSavedMoves = true;
//            possibleMoves = convertMinimaxMoves(movesFound);
//        } else {
        possibleMoves = board.getAllMoves(movingPlayer);
//        }
        if (possibleMoves.isEmpty()) {
            leavesReached++;
            return new MinimaxMove(m, value, depth, moveIndex, board, alphaBeta);
        }
        for (int i = 0, possibleMovesSize = possibleMoves.size(); i < possibleMovesSize; i++) {
            Move move = possibleMoves.get(i);
            if (isRoot) {
                m = move;
                moveIndex = i;
            }
            board.applyMove(move);

            MinimaxMove minimaxMove = minimax(board, !isMax, depth + 1, maxDepth, moveIndex, alphaBeta.clone(), m);

            board.undoMove(move);
            if (minimaxMove != null && minimaxMove.isBetterThan(bestMove) == isMax) {
                bestMove = new MinimaxMove(minimaxMove);
            }
            if (stopSearching(alphaBeta, bestMove, isMax, movingPlayer)) {
                break;
            }
        }

//        movesFound.add(new MinimaxMove(bestMove));
        return bestMove;
    }

    private boolean stopSearching(double[] alphaBeta, MinimaxMove bestMove, boolean isMax, int movingPlayer) {

        if (getElapsedSeconds() > controller.getScanTime() + controller.getScanTimeFlexibility()) {
            bestMove.setCompleteSearch(false);
            return true;
        }
        return prune(alphaBeta, bestMove, isMax, movingPlayer);
    }

    private ArrayList<Move> convertMinimaxMoves(ArrayList<MinimaxMove> minimaxMoves) {
        ArrayList<Move> ret = new ArrayList<>();
        for (MinimaxMove minimaxMove : minimaxMoves)
            ret.add(minimaxMove.getMove());
        return ret;
    }

    private boolean prune(double[] alphaBeta, MinimaxMove bestMove, boolean isMax, int player) {

        if (isMax) {
            alphaBeta[0] = Math.max(alphaBeta[0], bestMove.getMoveEvaluation().getEval());
        } else {
            alphaBeta[1] = Math.min(alphaBeta[1], bestMove.getMoveEvaluation().getEval());
        }
        return alphaBeta[1] <= alphaBeta[0];
//        return alphaBeta[0] <= alphaBeta[1] || bestMove.getMoveValue().didWin(player);
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


