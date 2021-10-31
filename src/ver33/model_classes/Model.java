package ver33.model_classes;

import Global_Classes.Positions;
import ver33.Controller;
import ver33.Location;
import ver33.model_classes.eval_classes.Eval;
import ver33.model_classes.eval_classes.Evaluation;
import ver33.model_classes.moves.MinimaxMove;
import ver33.model_classes.moves.Move;
import ver33.model_classes.pieces.Piece;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Model {

    public static final ConcurrentHashMap<Long, Transposition> transpositionsHashMap = new ConcurrentHashMap<>();
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
            MinimaxMove minimaxMove = negaMaxRoot(logicBoard, i);
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
            controller.clearAllHashMaps();
        }

        System.out.println("\nminimax move: " + bestMoveSoFar);
        System.out.println("max depth reached: " + (i - 1));
        System.out.println("num of positions reached: " + positionsReached);
        System.out.println("num of leaves reached: " + leavesReached);
        System.out.println("num of branches pruned: " + branchesPruned);
        System.out.println("num of transpositions hits: " + transpositionHits);

        return bestMoveSoFar;
    }


    private MinimaxMove negaMaxRoot(Board board, int maxDepth) {
//        MinimaxMove bestMove = new MinimaxMove(new Evaluation(true));
        MinimaxMove bestMove;
        ArrayList<Move> possibleMoves = board.generateAllMoves();
        Evaluation[] evals = new Evaluation[possibleMoves.size()];
        AtomicBoolean isCompleteSearch = new AtomicBoolean(true);

//        possibleMoves.stream().parallel().forEach(move -> {
        possibleMoves.forEach(move -> {

            if (!isOvertimeWithFlex()) {
                Board board1 = new Board(board);
                board1.applyMove(move);

                Evaluation eval = negaMax(board1, board.getCurrentPlayer(), 1, maxDepth, new AlphaBeta().alpha, new AlphaBeta().beta);
                eval.flipEval();
                evals[possibleMoves.indexOf(move)] = eval;
                move.setMoveEvaluation(eval);
                board1.undoMove(move);
            } else {

                isCompleteSearch.set(false);
            }

        });
//        for (int i = 0, evalsLength = evals.length; i < evalsLength; i++) {
//            Evaluation eval = evals[i];
//            if (eval != null && eval.isGreaterThan(bestMove.getMoveEvaluation())) {
//                bestMove = new MinimaxMove(possibleMoves.get(i), eval, 0);
//            }
//        }
        sortMoves(possibleMoves, true);
        bestMove = new MinimaxMove(possibleMoves.get(0), possibleMoves.get(0).getMoveEvaluation(), 0);
        bestMove.setCompleteSearch(isCompleteSearch.get());

        return bestMove;
    }

    private Evaluation negaMax(Board board, int player, int depth, int maxDepth, double alpha, double beta) {
        positionsReached++;
        Eval boardEval = board.getEval();
        long hash = board.getBoardHash().getFullHash();
        if (transpositionsHashMap.containsKey(hash)) {
            Transposition transposition = transpositionsHashMap.get(hash);
            if (transposition.getMaxDepth() >= maxDepth) {
                transpositionHits++;
                Evaluation eval = transposition.getEvaluation();
                if (transposition.getPlayer() != player) {
                    eval = eval.getEvalForBlack();
                }
                return eval;
            }
        }
        if (depth >= maxDepth || isOvertimeWithFlex() || boardEval.checkGameOver().isGameOver()) {
            leavesReached++;
            return boardEval.getCapturesEvaluation(player);
        }
        Evaluation bestEval = new Evaluation(true);
        ArrayList<Move> possibleMoves = board.generateAllMoves();

        for (Move move : possibleMoves) {
            board.applyMove(move);

            Evaluation eval = negaMax(board, player, depth + 1, maxDepth, -beta, -alpha);
            eval.flipEval();

            board.undoMove(move);

            move.setMoveEvaluation(eval);

            if (eval.isGreaterThan(bestEval)) {
                bestEval = eval;
            }

            alpha = Math.max(alpha, eval.getEval());
            if (beta <= alpha || (eval.isGameOver() && eval.getEval() > 0)) {
                branchesPruned++;
                break;
            }

        }
//        if (!transpositionsHashMap.containsKey(hash))
        sortMoves(possibleMoves, true);
        transpositionsHashMap.put(hash, new Transposition(maxDepth, player, bestEval));
        return bestEval;
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

        public AlphaBeta flipAndMinus() {
            AlphaBeta ret = new AlphaBeta(this);
            double t = ret.alpha;
            ret.alpha = -ret.beta;
            ret.beta = -t;
            return ret;
        }

        public boolean prune() {
            return beta <= alpha;
        }
    }
}


