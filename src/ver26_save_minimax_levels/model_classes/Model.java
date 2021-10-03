package ver26_save_minimax_levels.model_classes;

import Global_Packagaes.Positions;
import ver26_save_minimax_levels.Controller;
import ver26_save_minimax_levels.Location;
import ver26_save_minimax_levels.model_classes.eval_classes.Eval;
import ver26_save_minimax_levels.model_classes.eval_classes.Evaluation;
import ver26_save_minimax_levels.model_classes.pieces.Piece;
import ver26_save_minimax_levels.moves.MinimaxMove;
import ver26_save_minimax_levels.moves.Move;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;

public class Model {

    private final Controller controller;
    public Eval eval;
    private Board logicBoard;

    private MinimaxMove bestMoveSoFar;
    private ArrayList<MinimaxMove> movesFound;
    private ZonedDateTime minimaxStartedTime;
    private long positionsReached;
    private long leavesReached;
    private long branchesPruned;

    public Model(Controller controller) {
        this.controller = controller;
    }

    public void initGame(int startingPosition) {
        logicBoard = new Board(Positions.getAllPositions().get(startingPosition).getFen(), controller);
        eval = new Eval(logicBoard);
    }

    public void initGame(String fen) {
        logicBoard = new Board(fen, controller);
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

    public Piece getPiece(Location loc, Board board) {
        if (loc.isInBounds())
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
        return new double[]{-Evaluation.WIN_EVAL, Evaluation.WIN_EVAL};
    }

    // מהלך מינימקס
    public Move getAiMove() {
        System.out.println("Model getAiMove() using MINIMAX");
        initMinimaxTime();
        return getBestMoveUsingMinimax().getMove();
    }

    // ============================ for minimax ===========================
    private MinimaxMove getBestMoveUsingMinimax() {
        System.out.println("Current eval:\n" + logicBoard.getEval().getEvaluation() + Controller.HIDE_PRINT);
        bestMoveSoFar = null;
        positionsReached = -1;
        leavesReached = 0;
        branchesPruned = 0;
        movesFound = new ArrayList<>();
        int i = 1;
        while (getElapsedSeconds() < controller.getScanTime()) {
            positionsReached = -1;
            leavesReached = 0;

            String s = "-------------";
            System.out.println("\n" + s + "Starting search on depth " + i + s);
            MinimaxMove minimaxMove = minimax(i);
            System.out.println("depth " + i + " move: " + minimaxMove);
            i++;
            if (minimaxMove != null && (bestMoveSoFar == null || minimaxMove.isCompleteSearch() || minimaxMove.isBetterThan(bestMoveSoFar))) {
                bestMoveSoFar = minimaxMove;
            }
            assert bestMoveSoFar != null && bestMoveSoFar.getMove() != null;
            controller.clearAllDrawings();
            controller.drawMove(bestMoveSoFar.getMove(), Controller.MINIMAX_BEST_MOVE);
        }
        System.out.println("\nminimax move: " + bestMoveSoFar);
        System.out.println("max depth reached: " + i);
        System.out.println("num of positions reached: " + positionsReached);
        System.out.println("num of leaves reached: " + leavesReached);
        System.out.println("num of branches pruned: " + branchesPruned);
        return bestMoveSoFar;
    }


    public MinimaxMove minimax(int maxDepth) {
        //fixme copying board resetting starting locs
        return minimax(logicBoard, logicBoard.getCurrentPlayer(), true, 0, maxDepth, alphaBetaInit()[0], alphaBetaInit()[1], null);
    }

    public MinimaxMove minimax(Board board, int currentPlayer, boolean isMax, int depth, int maxDepth, double alpha, double beta, Move rootMove) {
        positionsReached++;

        if (depth >= maxDepth || board.getEval().isGameOver().isGameOver()) {
            leavesReached++;
            MinimaxMove ret = new MinimaxMove(rootMove, board.getEval().getEvaluation(currentPlayer), depth, board);
            return ret;
        }
        boolean isRoot = depth == 0;
        MinimaxMove bestMove = new MinimaxMove(new Evaluation(isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE));
        ArrayList<Move> possibleMoves = board.getAllMoves();

        sortMoves(possibleMoves);

        for (Move move : possibleMoves) {
            if (isRoot) {
                rootMove = move;
            }
            board.applyMove(move);

            MinimaxMove minimaxMove = minimax(board, currentPlayer, !isMax, depth + 1, maxDepth, alpha, beta, rootMove);

            board.undoMove(move);
            if (minimaxMove != null && minimaxMove.isBetterThan(bestMove) == isMax) {
                bestMove = new MinimaxMove(minimaxMove);
            }
            if (isMax) {
                alpha = Math.max(alpha, bestMove.getMoveEvaluation().getEval());
            } else {
                beta = Math.min(beta, bestMove.getMoveEvaluation().getEval());
            }
            if (beta <= alpha) {
                branchesPruned++;
                break;
            }
            if (stopSearching(bestMove)) {
                break;
            }
        }
        return bestMove;
    }

    private void sortMoves(ArrayList<Move> list) {
        Collections.sort(list);
        Collections.reverse(list);
    }

    private boolean stopSearching(MinimaxMove bestMove) {

        if (getElapsedSeconds() > controller.getScanTime() + controller.getScanTimeFlexibility()) {
            bestMove.setCompleteSearch(false);
            return true;
        }
        return false;
    }

    private ArrayList<Move> convertMinimaxMoves(ArrayList<MinimaxMove> minimaxMoves) {
        ArrayList<Move> ret = new ArrayList<>();
        for (MinimaxMove minimaxMove : minimaxMoves)
            ret.add(minimaxMove.getMove());
        return ret;
    }


    public void printAllPossibleMoves() {
        for (Move move : logicBoard.getAllMoves()) {
            System.out.println(move);
        }
    }

    public Controller getController() {
        return controller;
    }
}


