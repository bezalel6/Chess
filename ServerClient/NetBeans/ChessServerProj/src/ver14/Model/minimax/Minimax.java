package ver14.Model.minimax;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import ver14.Model.Eval.Book;
import ver14.Model.Eval.Eval;
import ver14.Model.Model;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.evaluation.Evaluation;
import ver14.SharedClasses.Game.moves.MinimaxMove;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.ThreadsUtil;

public class Minimax {

    private static final boolean USE_OPENING_BOOK = true;
    private static final int DEFAULT_FLEX = 0;
    public static boolean SHOW_UI = false;
    private final int scanTime;
    private final int scanTimeFlexibility;
    private final MinimaxView minimaxUI;
    private final CpuUsages cpuUsageRecords;
    private final Timer minimaxTimer;
    private Model model;
    private boolean log = false;
    //    private int numOfThreads = 1;
    private int numOfThreads = 4;
    private ZonedDateTime minimaxStartedTime;
    private long positionsReached;
    private long leavesReached;
    private long branchesPruned;
    private long transpositionHits;
    private boolean stillTheory;
    private ExecutorService threadPool;
    //    private ForkJoinPool threadPool;
    private MinimaxMove bestMove;
    private AtomicBoolean isCompleteSearch;
    private boolean interruptSearch;
    private boolean recordCpuUsage = false;
    private MyError interrupt = null;

    public Minimax(Model model, int scanTime) {
        this(model, scanTime, DEFAULT_FLEX);
    }

    public Minimax(Model model, int scanTime, int flexibility) {
        this.model = model;
        this.scanTimeFlexibility = flexibility;
        this.scanTime = scanTime;
        stillTheory = true;
        minimaxUI = new MinimaxView(SHOW_UI);
        if (SHOW_UI) {
            minimaxTimer = new Timer(150, l -> {
                minimaxUI.setTime(getElapsed(ChronoUnit.MILLIS));
            });
        } else {
            minimaxTimer = null;
        }
        cpuUsageRecords = new CpuUsages();
    }

    long getElapsed(ChronoUnit unit) {
        return minimaxStartedTime.until(ZonedDateTime.now(), unit);
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public void setRecordCpuUsage(boolean recordCpuUsage) {
        this.recordCpuUsage = recordCpuUsage;
    }

    public void end() {
        if (minimaxTimer != null) {
            minimaxTimer.stop();
        }
        minimaxUI.dispose();
        interruptSearch = true;
        if (threadPool != null) {
            threadPool.shutdown();
        }
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

    private MinimaxMove getBookMove() {
        if (USE_OPENING_BOOK && stillTheory) {
            String bookMove = Book.getBookMove(model);
            if (bookMove != null) {
                ArrayList<Move> possibleMoves = model.generateAllMoves();
                for (Move move : possibleMoves) {
                    if (move.getAnnotation().trim().equals(bookMove.trim())) {
                        return new MinimaxMove(move, Evaluation.book(), 0);
                    }
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
        log("Current eval:\n" + Eval.getEvaluation(model));
        MinimaxMove bookMove = getBookMove();
        if (bookMove != null) {
            return bookMove;
        }
        cpuUsageRecords.clear();
        MinimaxMove bestMoveSoFar = null;
        resetCounters();
        minimaxUI.setNumOfThreads(numOfThreads);
        int currentDepth = 1;
        boolean stop = false;
        initMinimaxTime();
        if (minimaxTimer != null) {
            minimaxTimer.start();
        }
        while (getElapsed(ChronoUnit.SECONDS) < scanTime && !stop) {
            positionsReached = -1;
            leavesReached = 0;

            String s = "-------------";
            log("\n" + s + "Starting search on depth " + currentDepth + s);
            minimaxUI.setCurrentDepth(currentDepth, getElapsed(ChronoUnit.MILLIS));

            MinimaxMove minimaxMove = minimaxRoot(model, currentDepth);

            if (interrupt != null) {
                throw interrupt;
            }

            if (recordCpuUsage) {
                int finalCurrentDepth = currentDepth;
                new Thread(() -> {
                    double usage = ThreadsUtil.sampleCpuUsage();
                    cpuUsageRecords.add(usage, finalCurrentDepth);
                    minimaxUI.setCpuUsage(usage);
                }).start();
            }
            log("depth " + currentDepth + " move: " + minimaxMove);
            currentDepth++;

            if (bestMoveSoFar == null || isCompleteSearch.get() || minimaxMove.isDeeperAndBetterThan(bestMoveSoFar)) {
                bestMoveSoFar = minimaxMove;
            }
            assert bestMoveSoFar.getMove() != null && bestMoveSoFar.getMoveEvaluation() != null;
            if (bestMoveSoFar.getMoveEvaluation().isGameOver()) {
                stop = true;
            }

            minimaxUI.setBestMoveSoFar(bestMoveSoFar.getMove());
            minimaxUI.setMoveEval(bestMoveSoFar.getMoveEvaluation());
        }
        if (minimaxTimer != null) {
            minimaxTimer.stop();
        }
        log("\nminimax move: " + bestMoveSoFar);
        log("max depth reached: " + (currentDepth - 1));
//        log("num of positions reached: " + positionsReached);
//        log("num of leaves reached: " + leavesReached);
//        log("num of branches pruned: " + branchesPruned);
//        log("num of transpositions hits: " + transpositionHits);

        return bestMoveSoFar;
    }

    private void resetCounters() {
        positionsReached = -1;
        leavesReached = 0;
        branchesPruned = 0;
        transpositionHits = 0;
    }

    private void log(String str) {
        if (log) {
            System.out.println("minimax->" + str);
        }
    }

    private MinimaxMove minimaxRoot(Model model, int maxDepth) {
        isCompleteSearch = new AtomicBoolean(true);
//        threadPool = Executors.newFixedThreadPool(numOfThreads);
        threadPool = new ForkJoinPool(numOfThreads);

        Evaluation[] evals = startMultithreaded(model, model.getCurrentPlayer(), maxDepth);

        if (interrupt != null) {
            throw interrupt;
        }

        int bestMoveIndex = 0;
        Evaluation bestEval = null;
        ArrayList<Move> possibleMoves = model.generateAllMoves();
        for (int i = 0, possibleMovesSize = possibleMoves.size(); i < possibleMovesSize; i++) {
            Move move = possibleMoves.get(i);

            move.setMoveEvaluation(evals[i]);
            if (evals[i] != null) {
                if (evals[i].isGreaterThan(bestEval)) {
                    bestEval = evals[i];
                    bestMoveIndex = i;
                }
            }
        }

        bestMove = new MinimaxMove(possibleMoves.get(bestMoveIndex), bestEval, 0);
        bestMove.setCompleteSearch(isCompleteSearch.get());

        return bestMove;
    }

    private Evaluation[] startMultithreaded(Model model, PlayerColor minimaxPlayerColor, int maxDepth) {
        ArrayList<Move> possibleMoves = model.generateAllMoves();
        int numOfPossibleMoves = possibleMoves.size();
        Evaluation[] evals = new Evaluation[numOfPossibleMoves];

        threadPool.execute(() -> {
            possibleMoves.stream().parallel().forEach(move -> {
                try {
                    if (!isOvertime()) {
                        Model model1 = new Model(model);
                        model1.applyMove(move);
                        Evaluation eval = minimax(new MinimaxParameters(model1, false, maxDepth, minimaxPlayerColor, move));
                        model1.undoMove(move);

                        move.setMoveEvaluation(eval);

                        evals[possibleMoves.indexOf(move)] = eval;
                    } else {
                        isCompleteSearch.set(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

        });
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                throw new Error("thread pool is acting up");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return evals;
    }

    private Evaluation minimax(MinimaxParameters parms) {
//        positionsReached++;

        if (interrupt != null) {
            throw interrupt;
        }
        if (isOvertime() || Eval.isGameOver(parms.model) || parms.currentDepth >= parms.maxDepth) {
//            leavesReached++;
            Evaluation evaluation = Eval.getEvaluation(parms.model, parms.minimaxPlayerColor);
            evaluation.setEvaluationDepth(parms.currentDepth / 2);
            return evaluation;
        }

        return executeMovesMinimax(parms);
    }

    private Evaluation executeMovesMinimax(MinimaxParameters parms) {
        Evaluation bestEval = null;
        ArrayList<Move> possibleMoves = parms.model.generateAllMoves();
        sortMoves(possibleMoves, true);
        for (int i = 0, possibleMovesSize = possibleMoves.size(); i < possibleMovesSize; i++) {
            Move move = possibleMoves.get(i);

            if (interrupt != null) {
                throw interrupt;
            }
            parms.model.applyMove(move);

            Evaluation eval = minimax(parms.nextDepth());

            parms.model.undoMove(move);

            move.setMoveEvaluation(eval);

            if (bestEval == null || eval.isGreaterThan(bestEval) == parms.isMax) {
                bestEval = eval;
            }

            if (parms.prune(bestEval)) {
                break;
            }

        }
        return bestEval;
    }

    public CpuUsages getCpuUsageRecords() {
        return cpuUsageRecords;
    }

//    private Evaluation getTranspositionEval(MinimaxParameters parms, long hash) {
//        if (transpositionsHashMap.containsKey(hash)) {
//            Transposition transposition = transpositionsHashMap.get(hash);
//            if (transposition.getMaxDepth() >= parms.maxDepth) {
//                transpositionHits++;
//                return new Evaluation(transposition.getEvaluation());
//            }
//        }
//        return null;
//    }
    private void sortMoves(ArrayList<Move> list, boolean isMax) {
        Collections.sort(list);
        if (isMax) {
            Collections.reverse(list);
        }
    }

    private boolean isOvertime() {
        return getElapsed(ChronoUnit.SECONDS) > scanTime + scanTimeFlexibility;
    }

    public void interrupt(MyError error) {
        this.interrupt = error;
    }

    public static class CpuUsages {

        private final ArrayList<Double> usages;
        private final ArrayList<Integer> depths;

        public CpuUsages() {
            depths = new ArrayList<>();
            usages = new ArrayList<>();
        }

        public void add(double usage, int depth) {
            usages.add(usage);
            depths.add(depth);
        }

        public ArrayList<Double> getUsages() {
            return usages;
        }

        public ArrayList<Integer> getDepths() {
            return depths;
        }

        public void clear() {
            usages.clear();
            depths.clear();
        }
    }
}