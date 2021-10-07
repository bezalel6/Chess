package ver27_minimax_levels.model_classes;

import Global_Classes.Positions;
import ver27_minimax_levels.Controller;
import ver27_minimax_levels.Location;
import ver27_minimax_levels.model_classes.eval_classes.Eval;
import ver27_minimax_levels.model_classes.eval_classes.Evaluation;
import ver27_minimax_levels.model_classes.moves.MinimaxMove;
import ver27_minimax_levels.model_classes.moves.Move;
import ver27_minimax_levels.model_classes.pieces.Piece;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;

public class Model {

    private final Controller controller;
    public Eval eval;
    private Board logicBoard;

    private MinimaxMove bestMoveSoFar;
    private ArrayList<MinimaxMove> minimaxMoves;
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

    // מהלך מינימקס
    public Move getAiMove() {
        System.out.println("Model getAiMove() using MINIMAX");
        initMinimaxTime();
        return getBestMoveUsingMinimax().getMove();
    }

    // ============================ for minimax ===========================
    private MinimaxMove getBestMoveUsingMinimax() {
        System.out.println("Current eval:\n" + logicBoard.getEvaluation() + Controller.HIDE_PRINT);
        bestMoveSoFar = null;
        positionsReached = -1;
        leavesReached = 0;
        branchesPruned = 0;
        minimaxMoves = new ArrayList<>();
        int i = 1;
        boolean stop = false;
        while (getElapsedSeconds() < controller.getScanTime() && !stop) {
            positionsReached = -1;
            leavesReached = 0;

            String s = "-------------";
            System.out.println("\n" + s + "Starting search on depth " + i + s);
            MinimaxMove minimaxMove = minimax(i);
            System.out.println("depth " + i + " move: " + minimaxMove);
            i++;
            if (minimaxMove != null && (bestMoveSoFar == null || minimaxMove.isCompleteSearch() || minimaxMove.isDeeperAndBetterThan(bestMoveSoFar))) {
                bestMoveSoFar = minimaxMove;
            }
            assert bestMoveSoFar != null && bestMoveSoFar.getMove() != null;
            if (bestMoveSoFar.getMoveEvaluation().isGameOver()) {
                stop = true;
            }
            controller.clearAllDrawings();
            controller.drawMove(bestMoveSoFar.getMove(), Controller.MINIMAX_BEST_MOVE);
        }

        System.out.println("\nminimax move: " + bestMoveSoFar);
        System.out.println("max depth reached: " + (i - 1));
        System.out.println("num of positions reached: " + positionsReached);
        System.out.println("num of leaves reached: " + leavesReached);
        System.out.println("num of branches pruned: " + branchesPruned);
        System.out.println("moves: ");
        Collections.sort(minimaxMoves);
//        Collections.reverse(minimaxMoves);//if you want the list to go in descending order
        for (MinimaxMove minimaxMove : minimaxMoves) {
            System.out.println(minimaxMove.getShortPrintingStr());
        }
        return bestMoveSoFar;
    }


    public MinimaxMove minimax(int maxDepth) {
        //fixme copying board resetting starting locs
        return minimax(new Board(logicBoard), logicBoard.getCurrentPlayer(), true, 0, maxDepth, -Evaluation.WIN_EVAL, Evaluation.WIN_EVAL, null);
    }

    public MinimaxMove minimax(Board board, int currentPlayer, boolean isMax, int depth, int maxDepth, double alpha, double beta, Move rootMove) {
        positionsReached++;
        Eval boardEval = board.getEval();
        if (depth >= maxDepth || isOvertimeWithFlex() || boardEval.checkGameOver().isGameOver()) {
            leavesReached++;
            Evaluation moveEval = boardEval.getEvaluation(currentPlayer);
            rootMove.setMoveEvaluation(moveEval);
            MinimaxMove ret = new MinimaxMove(rootMove, moveEval, depth, board);
            if (isOvertimeWithFlex()) {
                ret.setCompleteSearch(false);
            }
            return ret;
        }
        boolean isRoot = depth == 0;
        MinimaxMove bestMove = new MinimaxMove(new Evaluation(isMax));
        ArrayList<Move> possibleMoves = board.getAllMoves();
        if (possibleMoves.size() == 1) {
            if (isRoot) {
                rootMove = possibleMoves.get(0);
            }
            return new MinimaxMove(rootMove, boardEval.getEvaluation(currentPlayer), depth);
        }
        sortMoves(possibleMoves, isMax);
        for (Move move : possibleMoves) {
            if (isRoot) {
                rootMove = move;
            }
            board.applyMove(move);

            MinimaxMove minimaxMove = minimax(board, currentPlayer, !isMax, depth + 1, maxDepth, alpha, beta, rootMove);

            board.undoMove(move);
            if (isRoot) {
                minimaxMoves.add(new MinimaxMove(minimaxMove));
            }
            if (minimaxMove.isBetterThan(bestMove) == isMax) {
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
        }
        if (bestMove.getMove() == null) {
            bestMove = new MinimaxMove(rootMove, boardEval.getEvaluation(currentPlayer), depth);
        }
        return bestMove;
    }

    private void sortMoves(ArrayList<Move> list, boolean isMax) {
        Collections.sort(list);
        if (isMax)
            Collections.reverse(list);
    }

    private boolean isOvertimeWithFlex(MinimaxMove bestMove) {

        if (getElapsedSeconds() > controller.getScanTime() + controller.getScanTimeFlexibility()) {
            bestMove.setCompleteSearch(false);
            return true;
        }
        return false;
    }

    private boolean isOvertimeWithFlex() {
        return getElapsedSeconds() > controller.getScanTime() + controller.getScanTimeFlexibility();
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


