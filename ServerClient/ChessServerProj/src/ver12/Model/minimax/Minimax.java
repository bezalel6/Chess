package ver12.Model.minimax;

import ver12.Model.Eval.Book;
import ver12.Model.Eval.Eval;
import ver12.Model.Model;
import ver12.Model.hashing.HashManager;
import ver12.Model.hashing.Transposition;
import ver12.Model.hashing.my_hash_maps.MyHashMap;
import ver12.SharedClasses.PlayerColor;
import ver12.SharedClasses.evaluation.Evaluation;
import ver12.SharedClasses.moves.MinimaxMove;
import ver12.SharedClasses.moves.Move;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class Minimax {
    public static final MyHashMap transpositionsHashMap = new MyHashMap(HashManager.Size.TRANSPOSITIONS);
    static final boolean USE_OPENING_BOOK = true;
    private static final int DEFAULT_FLEX = 0;
    private final Model model;
    private final int scanTime;
    private final int scanTimeFlexibility;
    private final MinimaxView minimaxUI;

    private ZonedDateTime minimaxStartedTime;
    private long positionsReached;
    private long leavesReached;
    private long branchesPruned;
    private long transpositionHits;
    private boolean stillTheory;
    private ExecutorService threadPool;
    private int numOfThreads = 4;
    private MinimaxMove bestMove;
    private AtomicBoolean isCompleteSearch;
    private boolean interruptSearch;

    public Minimax(Model model, int scanTime) {
        this(model, scanTime, DEFAULT_FLEX);
    }

    public Minimax(Model model, int scanTime, int flexibility) {
        this.model = model;
        this.scanTimeFlexibility = flexibility;
        this.scanTime = scanTime;
        stillTheory = true;
        minimaxUI = new MinimaxView();
    }

    public void end() {
        minimaxUI.dispose();
        if (threadPool != null)
            threadPool.shutdown();
    }

    public long getPositionsReached() {
        return positionsReached;
    }

    public boolean isStillTheory() {
        return stillTheory;
    }

    public void setStillTheory(boolean stillTheory) {
        this.stillTheory = stillTheory;
    }

    public void setNumOfThreads(int numOfThreads) {
        this.numOfThreads = numOfThreads;
    }

    void initMinimaxTime() {
        minimaxStartedTime = ZonedDateTime.now();
    }

    long getElapsedSeconds() {
        long ret = minimaxStartedTime.until(ZonedDateTime.now(), ChronoUnit.SECONDS);
        minimaxUI.setTime(ret);
        return ret;
    }

    private MinimaxMove getBookMove() {
        if (USE_OPENING_BOOK && stillTheory) {
            String bookMove = Book.getBookMove(model);
            if (bookMove != null) {
                ArrayList<Move> possibleMoves = model.generateAllMoves();
                for (Move move : possibleMoves) {
                    if (move.getAnnotation().trim().equals(bookMove.trim()))
                        return new MinimaxMove(move, Evaluation.book(), 0);
                }
            }
            stillTheory = false;
        }
        return null;
    }// ============================ for minimax ===========================

    public Move getBestMove() {
        minimaxUI.resetBestMoves();
        MinimaxMove minimaxMove = getBestMoveUsingMinimax();
        Move move = minimaxMove.getMove();
        minimaxUI.setBestMoveSoFar();
        minimaxUI.setChosenMove(move.getAnnotation());
        return move;
    }

    private MinimaxMove getBestMoveUsingMinimax() {
        MinimaxMove bookMove = getBookMove();
        if (bookMove != null)
            return bookMove;
        log("Current eval:\n" + Eval.getEvaluation(model));
        MinimaxMove bestMoveSoFar = null;
        positionsReached = -1;
        leavesReached = 0;
        branchesPruned = 0;
        transpositionHits = 0;
        initMinimaxTime();
        int currentDepth = 1;
        boolean stop = false;
        while (getElapsedSeconds() < scanTime && !stop) {
            positionsReached = -1;
            leavesReached = 0;

            String s = "-------------";
            log("\n" + s + "Starting search on depth " + currentDepth + s);

            minimaxUI.setCurrentDepth(currentDepth);

            MinimaxMove minimaxMove = minimaxRoot(model, currentDepth);
            log("depth " + currentDepth + " move: " + minimaxMove);
            currentDepth++;

            if (bestMoveSoFar == null || minimaxMove.isCompleteSearch() || minimaxMove.isDeeperAndBetterThan(bestMoveSoFar)) {
                bestMoveSoFar = minimaxMove;
            }
            assert bestMoveSoFar.getMove() != null;
            if (bestMoveSoFar.getMoveEvaluation() == null || bestMoveSoFar.getMoveEvaluation().isGameOver()) {
                stop = true;
            }

            minimaxUI.setBestMoveSoFar(bestMoveSoFar.getMove());
            minimaxUI.setMoveEval(bestMoveSoFar.getMoveEvaluation());
        }

        log("\nminimax move: " + bestMoveSoFar);
        log("max depth reached: " + (currentDepth - 1));
        log("num of positions reached: " + positionsReached);
        log("num of leaves reached: " + leavesReached);
        log("num of branches pruned: " + branchesPruned);
        log("num of transpositions hits: " + transpositionHits);

        return bestMoveSoFar;
    }

    private void log(String str) {

    }

    MinimaxMove minimaxRoot(Model model, int maxDepth) {
        isCompleteSearch = new AtomicBoolean(true);
        threadPool = Executors.newFixedThreadPool(numOfThreads);

        Evaluation[] evals = startMultithreadedMinimax(model, model.getCurrentPlayer(), maxDepth);
        endMultithreadedMinimax();

        ArrayList<Move> possibleMoves = model.generateAllMoves();
        for (int i = 0, possibleMovesSize = possibleMoves.size(); i < possibleMovesSize; i++) {
            Move move = possibleMoves.get(i);
            move.setMoveEvaluation(evals[i]);
        }
        sortMoves(possibleMoves, true);
        bestMove = new MinimaxMove(possibleMoves.get(0), possibleMoves.get(0).getMoveEvaluation(), 0);
        bestMove.setCompleteSearch(isCompleteSearch.get());

        return bestMove;
    }

    private Evaluation[] startMultithreadedMinimax(Model model, PlayerColor minimaxPlayerColor, int maxDepth) {
        ArrayList<Move> possibleMoves = model.generateAllMoves();
        int numOfPossibleMoves = possibleMoves.size();
        Evaluation[] evals = new Evaluation[numOfPossibleMoves];

        IntStream.range(0, numOfPossibleMoves).forEach(index -> {
            threadPool.execute(() -> {
                if (!isOvertimeWithFlex()) {
                    Move move = possibleMoves.get(index);
                    Model model1 = new Model(model);
                    model1.applyMove(move);
                    Evaluation eval = minimax(new MinimaxParameters(model1, false, maxDepth, minimaxPlayerColor, move));
                    model1.undoMove();

                    move.setMoveEvaluation(eval);

                    evals[index] = eval;
                } else {
                    isCompleteSearch.set(false);
                }
            });
        });

        return evals;
    }

    private void endMultithreadedMinimax() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                log("had to stop the thingy");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Evaluation minimax(MinimaxParameters parms) {
        positionsReached++;

        long hash = parms.model.getBoardHash().getFullHash();

        Evaluation transpositionEval = getTranspositionEval(parms, hash);
        if (transpositionEval != null)
            return transpositionEval;

        if (interruptSearch || isOvertimeWithFlex() || Eval.isGameOver(parms.model) || parms.currentDepth >= parms.maxDepth) {
            leavesReached++;
            Evaluation evaluation = Eval.getEvaluation(parms.model, parms.minimaxPlayerColor);
            evaluation.setEvaluationDepth(parms.currentDepth / 2);
            return evaluation;
        }

        Evaluation bestEval = executeMovesMinimax(parms);

        transpositionsHashMap.put(hash, new Transposition(parms, bestEval));
        return bestEval;
    }

    private Evaluation executeMovesMinimax(MinimaxParameters parms) {
        Evaluation bestEval = null;
        ArrayList<Move> possibleMoves = parms.model.generateAllMoves();
        sortMoves(possibleMoves, true);
        for (Move move : possibleMoves) {

            if (interruptSearch) {
//                might cause a null return
                break;
            }
            parms.model.applyMove(move);

            Evaluation eval = minimax(parms.nextDepth());

            parms.model.undoMove();

            move.setMoveEvaluation(eval);

            if (bestEval == null || eval.isGreaterThan(bestEval) == parms.isMax) {
                bestEval = eval;
            }

            if (parms.isMax) {
                parms.a = Math.max(parms.a, bestEval.getEval());
            } else {
                parms.b = Math.min(parms.b, bestEval.getEval());
            }
            if (parms.b <= parms.a) {
                branchesPruned++;
                break;
            }

        }
//        assert bestEval != null;
        return bestEval;
    }

    private Evaluation getTranspositionEval(MinimaxParameters parms, long hash) {
        if (transpositionsHashMap.containsKey(hash)) {
            Transposition transposition = (Transposition) transpositionsHashMap.get(hash);
            if (transposition.getMaxDepth() >= parms.maxDepth) {
                transpositionHits++;
                return new Evaluation(transposition.getEvaluation());
            }
        }
        return null;
    }

    private void sortMoves(ArrayList<Move> list, boolean isMax) {
        Collections.sort(list);
        if (isMax)
            Collections.reverse(list);
    }

    private boolean isOvertimeWithFlex() {
        return getElapsedSeconds() > scanTime + scanTimeFlexibility;
    }

    public static class MinimaxParameters {
        private final Model model;
        private final boolean isMax;
        private final PlayerColor minimaxPlayerColor;
        private final int currentDepth, maxDepth;
        private final Move rootMove;
        private double a, b;

        public MinimaxParameters(Model model, boolean isMax, int maxDepth, PlayerColor minimaxPlayerColor, Move rootMove) {
            this(model, isMax, 1, maxDepth, minimaxPlayerColor, -Evaluation.WIN_EVAL, Evaluation.WIN_EVAL, rootMove);
        }

        public MinimaxParameters(Model model, boolean isMax, int currentDepth, int maxDepth, PlayerColor minimaxPlayerColor, double a, double b, Move rootMove) {
            this.model = model;
            this.isMax = isMax;
            this.maxDepth = maxDepth;
            this.minimaxPlayerColor = minimaxPlayerColor;
            this.currentDepth = currentDepth;
            this.a = a;
            this.b = b;
            this.rootMove = rootMove;

        }

        public Model getBoard() {
            return model;
        }

        public boolean isMax() {
            return isMax;
        }

        public PlayerColor getMinimaxPlayer() {
            return minimaxPlayerColor;
        }

        public int getCurrentDepth() {
            return currentDepth;
        }

        public int getMaxDepth() {
            return maxDepth;
        }

        public double getA() {
            return a;
        }

        public double getB() {
            return b;
        }

        public MinimaxParameters nextDepth() {
            return new MinimaxParameters(model, !isMax, currentDepth + 1, maxDepth, minimaxPlayerColor, a, b, rootMove);
        }
    }
}