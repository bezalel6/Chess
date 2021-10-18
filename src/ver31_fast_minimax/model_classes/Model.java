package ver31_fast_minimax.model_classes;

import Global_Classes.Positions;
import ver31_fast_minimax.Controller;
import ver31_fast_minimax.Location;
import ver31_fast_minimax.model_classes.eval_classes.Eval;
import ver31_fast_minimax.model_classes.eval_classes.Evaluation;
import ver31_fast_minimax.model_classes.moves.MinimaxMove;
import ver31_fast_minimax.model_classes.moves.Move;
import ver31_fast_minimax.model_classes.pieces.Pawn;
import ver31_fast_minimax.model_classes.pieces.Piece;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class Model {

    private static final ConcurrentHashMap<Long, Transposition> transpositionsHashMap = new ConcurrentHashMap<>();
    private final Controller controller;
    public Eval eval;
    private Board logicBoard;
    private ZonedDateTime minimaxStartedTime;
    private long positionsReached;
    private long leavesReached;
    private long branchesPruned;
    private long transpositionHits;

    public Model(Controller controller) {
        this.controller = controller;
    }

    public void initGame(int startingPosition) {
        logicBoard = new Board(Positions.getAllPositions().get(startingPosition).getFen());
        eval = new Eval(logicBoard);
    }

    public void initGame(String fen) {
        logicBoard = new Board(fen);
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
        MinimaxMove bestMoveSoFar = null;
        positionsReached = -1;
        leavesReached = 0;
        branchesPruned = 0;
        transpositionHits = 0;
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

            if (bestMoveSoFar == null || minimaxMove.isCompleteSearch() || minimaxMove.isDeeperAndBetterThan(bestMoveSoFar)) {
                bestMoveSoFar = minimaxMove;
            }
            assert bestMoveSoFar.getMove() != null;
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
        System.out.println("num of transpositions hits: " + transpositionHits);
//        System.out.println("moves: ");
//        for (MinimaxMove minimaxMove : minimaxMoves) {
//            System.out.println(minimaxMove.getShortPrintingStr());
//        }
        return bestMoveSoFar;
    }


    public MinimaxMove minimax(int maxDepth) {
        return minimax(new Board(logicBoard), logicBoard.getCurrentPlayer(), true, 0, maxDepth, new AlphaBeta(), null);
    }

    public MinimaxMove minimax(Board board, int currentPlayer, boolean isMax, int depth, int maxDepth, AlphaBeta alphaBeta, Move rootMove) {
        positionsReached++;
        Eval boardEval = board.getEval();
        if (depth >= maxDepth || isOvertimeWithFlex() || boardEval.checkGameOver().isGameOver()) {
            leavesReached++;
            Evaluation moveEval = boardEval.getEvaluation(currentPlayer);
            rootMove.setMoveEvaluation(moveEval);
            if (false)
                board = new Board(board);
            MinimaxMove ret = new MinimaxMove(rootMove, moveEval, depth, board, isMax);

            if (isOvertimeWithFlex()) {
                ret.setCompleteSearch(false);
            }
            return ret;
        }
        MinimaxMove bestMove;
//        if (transpositionsHashMap.containsKey(hash) && transpositionsHashMap.get(hash).getMaxDepth() > maxDepth) {
//            transpositionHits++;
//            Transposition transposition = transpositionsHashMap.get(hash);
//            bestMove = transpositionMinimax(transposition, currentPlayer, isMax, maxDepth, isRoot);
        if (false) {
            bestMove = parallelMinimax(board, isMax, currentPlayer, depth, maxDepth, alphaBeta);
        } else {
            bestMove = normalMinimax(board, isMax, currentPlayer, depth, maxDepth, alphaBeta, rootMove);
        }

//        assert bestMove.getMove() != null;
        return bestMove;
    }

    private MinimaxMove parallelMinimax(Board board, boolean isMax, int currentPlayer, int depth, int maxDepth, AlphaBeta alphaBeta) {
        MinimaxMove bestMove = new MinimaxMove(new Evaluation(isMax));
        ArrayList<Move> possibleMoves = board.generateAllMoves();
//        sortMoves(possibleMoves, isMax);
        int size = possibleMoves.size();
        final MinimaxMove[] minimaxMoves = new MinimaxMove[size];
        AtomicBoolean outOfTime = new AtomicBoolean(false);
        IntStream.range(0, size).parallel().forEach(i -> {
            if (!outOfTime.get()) {
                Board board1 = new Board(board);
                Move move = possibleMoves.get(i);
                board1.applyMove(move);

                minimaxMoves[i] = minimax(board1, currentPlayer, !isMax, depth + 1, maxDepth, new AlphaBeta(alphaBeta), move);
                if (!minimaxMoves[i].isCompleteSearch()) {
                    outOfTime.set(true);
                }
                board1.undoMove(move);
            }
        });
        for (MinimaxMove minimaxMove : minimaxMoves) {
            if (minimaxMove != null && minimaxMove.isBetterThan(bestMove) == isMax) {
                bestMove = new MinimaxMove(minimaxMove);
            }
        }
        return bestMove;
    }

    private MinimaxMove normalMinimax(Board board, boolean isMax, int currentPlayer, int depth, int maxDepth, AlphaBeta alphaBeta, Move rootMove) {
        MinimaxMove bestMove = new MinimaxMove(new Evaluation(isMax));
        ArrayList<Move> possibleMoves = board.generateAllMoves();
        boolean isRoot = depth == 0;

        for (Move move : possibleMoves) {
            board.applyMove(move);

            MinimaxMove minimaxMove = minimax(board, currentPlayer, !isMax, depth + 1, maxDepth, new AlphaBeta(alphaBeta), isRoot ? move : rootMove);

            board.undoMove(move);

            if (minimaxMove.isBetterThan(bestMove) == isMax) {
                bestMove = new MinimaxMove(minimaxMove);
            }
            if (isMax) {
                alphaBeta.alpha = Math.max(alphaBeta.alpha, bestMove.getMoveEvaluation().getEval());
            } else {
                alphaBeta.beta = Math.min(alphaBeta.beta, bestMove.getMoveEvaluation().getEval());
            }
            if (alphaBeta.prune()) {
                branchesPruned++;
                break;
            }
        }
        return bestMove;
    }

    private MinimaxMove transpositionMinimax(Transposition transposition, int currentPlayer, boolean isMax, int maxDepth, boolean isRoot) {
        MinimaxMove bestMove = new MinimaxMove(new Evaluation(isMax));
        ArrayList<MinimaxMove> bottomMoves = transposition.getBottomMoves();
        final MinimaxMove[] minimaxMoves = new MinimaxMove[bottomMoves.size()];
        int size = bottomMoves.size();
        if (isRoot) {
            IntStream.range(0, size).parallel().forEach(i -> {
                MinimaxMove loadedMinimaxMove = bottomMoves.get(i);
                Board board = loadedMinimaxMove.getBoard();
                int depth = loadedMinimaxMove.getMoveDepth();
                Board board1 = new Board(board);
                minimaxMoves[i] = minimax(board1, currentPlayer, isMax, 0, maxDepth, new AlphaBeta(), null);
            });
            for (MinimaxMove minimaxMove : minimaxMoves) {
                if (minimaxMove.isBetterThan(bestMove) == isMax) {
                    bestMove = new MinimaxMove(minimaxMove);
                }
            }

        } else {
            for (MinimaxMove loadedMinimaxMove : bottomMoves) {
                Board board = loadedMinimaxMove.getBoard();
                int depth = loadedMinimaxMove.getMoveDepth();
                MinimaxMove minimaxMove = minimax(board, currentPlayer, isMax, 0, maxDepth, new AlphaBeta(), null);
                if (minimaxMove.isBetterThan(bestMove) == isMax) {
                    bestMove = new MinimaxMove(minimaxMove);
                }

            }
        }
        return bestMove;
    }

    private void createOrAddToTransposition(long hash, MinimaxMove minimaxMove, int maxDepth) {
        if (transpositionsHashMap.containsKey(hash) && transpositionsHashMap.get(hash).getMaxDepth() > maxDepth) {
            Transposition transposition = transpositionsHashMap.get(hash);
            transposition.addMove(minimaxMove);
        } else {
            transpositionsHashMap.put(hash, new Transposition(minimaxMove, maxDepth));
        }

    }

    private void sortMoves(ArrayList<Move> list, boolean isMax) {
        Collections.sort(list);
        if (isMax)
            Collections.reverse(list);
    }

    private boolean isOvertimeWithFlex() {
        return getElapsedSeconds() > controller.getScanTime() + controller.getScanTimeFlexibility();
    }

    public void printAllPossibleMoves() {
        for (Move move : logicBoard.generateAllMoves()) {
            System.out.println(move);
        }
    }
//
//    private ArrayList<Move> convertMinimaxMoves(ArrayList<MinimaxMove> minimaxMoves) {
//        ArrayList<Move> ret = new ArrayList<>();
//        for (MinimaxMove minimaxMove : minimaxMoves)
//            ret.add(minimaxMove.getMove());
//        return ret;
//    }

    public Controller getController() {
        return controller;
    }

    static class AlphaBeta {
        private double alpha, beta;

        public AlphaBeta() {
            alpha = -Evaluation.WIN_EVAL;
            beta = Evaluation.WIN_EVAL;
        }

        public AlphaBeta(AlphaBeta other) {
            this.alpha = other.alpha;
            this.beta = other.beta;
        }

        public boolean prune() {
            return beta <= alpha;
        }
    }
}


