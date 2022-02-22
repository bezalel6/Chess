package ver4.model_classes;

import ver4.SharedClasses.Location;
import ver4.SharedClasses.PlayerColor;
import ver4.SharedClasses.StrUtils;
import ver4.SharedClasses.evaluation.Evaluation;
import ver4.SharedClasses.moves.BasicMove;
import ver4.SharedClasses.moves.MinimaxMove;
import ver4.SharedClasses.moves.Move;
import ver4.model_classes.eval_classes.Book;
import ver4.model_classes.eval_classes.Eval;

import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class Minimax {
    public static final ConcurrentHashMap<Long, Transposition> transpositionsHashMap = new ConcurrentHashMap<>();
    static final boolean USE_OPENING_BOOK = false;
    private static final int DEFAULT_FLEX = 0;
    private static String EMPTY = "-";
    private final Model model;
    private final int scanTime;
    private final int scanTimeFlexibility;
    private final JFrame minimaxUI;
    private final JLabel timeLbl;
    private final JLabel bestMoveSoFarLbl;
    private final JLabel chosenMoveLbl;
    private final JLabel currentDepthLbl;
    private final JLabel moveEvaluationLbl;
    private ZonedDateTime minimaxStartedTime;
    private long positionsReached;
    private long leavesReached;
    private long branchesPruned;
    private long transpositionHits;
    private boolean stillTheory;
    private ExecutorService pool;
    private int numOfThreads = 8;
    private MinimaxMove bestMove;
    private AtomicBoolean isCompleteSearch;

    public Minimax(Model model, int scanTime, int flexibility) {
        this.model = model;
        this.scanTimeFlexibility = flexibility;
        this.scanTime = scanTime;
        stillTheory = true;
        Font font = new Font(null, Font.BOLD, 30);

        timeLbl = new JLabel(StrUtils.createTimeStr(0)) {{
            setFont(font);
        }};
        bestMoveSoFarLbl = new JLabel(EMPTY) {{
            setFont(font);
        }};
        chosenMoveLbl = new JLabel(EMPTY) {{
            setFont(font);
        }};
        currentDepthLbl = new JLabel(EMPTY) {{
            setFont(font);
        }};
        moveEvaluationLbl = new JLabel(EMPTY) {{
            setFont(font);
        }};

        this.minimaxUI = new JFrame() {{
            setSize(800, 200);
            setLayout(new GridLayout(0, 2));

            add(new JLabel("Time Elapsed: ") {{
                setFont(font);
            }});
            add(timeLbl);

            add(new JLabel("Current Depth: ") {{
                setFont(font);
            }});
            add(currentDepthLbl);

            add(new JLabel("Best Move So Far: ") {{
                setFont(font);
            }});
            add(bestMoveSoFarLbl);

            add(new JLabel("Chosen Move: ") {{
                setFont(font);
            }});
            add(chosenMoveLbl);

            add(new JLabel("Move Evaluation: ") {{
                setFont(font);
            }});
            add(moveEvaluationLbl);

            pack();
        }};

        minimaxUI.setAlwaysOnTop(true);
        minimaxUI.setVisible(true);
    }

    public Minimax(Model model, int scanTime) {
        this(model, scanTime, DEFAULT_FLEX);
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
        timeLbl.setText(StrUtils.createTimeStr(TimeUnit.SECONDS.toMillis(ret)));
        return ret;
    }

    public Move getBestMoveUsingStockfish() {
        String m = new Stockfish().getBestMove(model.getFenStr(), 1000 + new Random().nextInt(1000));
        BasicMove basicMove = new BasicMove(Location.getLoc(m.substring(0, 2)), Location.getLoc(m.substring(2)));
        return model.findMove(basicMove);
//        return null;
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
        bestMoveSoFarLbl.setText(EMPTY);
        chosenMoveLbl.setText(EMPTY);
        MinimaxMove minimaxMove = getBestMoveUsingMinimax();
        Move move = minimaxMove.getMove();
//        Move move = getBestMoveUsingStockfish();
        bestMoveSoFarLbl.setText(EMPTY);
        chosenMoveLbl.setText(move.getAnnotation());
        return move;
    }

    private MinimaxMove getBestMoveUsingMinimax() {
        MinimaxMove bookMove = getBookMove();
        if (bookMove != null)
            return bookMove;
        System.out.println("Current eval:\n" + Eval.getEvaluation(model));
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
            System.out.println("\n" + s + "Starting search on depth " + currentDepth + s);

            currentDepthLbl.setText(currentDepth + "");

            MinimaxMove minimaxMove = minimaxRoot(model, currentDepth);
            System.out.println("depth " + currentDepth + " move: " + minimaxMove);
            currentDepth++;

            if (bestMoveSoFar == null || minimaxMove.isCompleteSearch() || minimaxMove.isDeeperAndBetterThan(bestMoveSoFar)) {
                bestMoveSoFar = minimaxMove;
            }
            assert bestMoveSoFar.getMove() != null;
            if (bestMoveSoFar.getMoveEvaluation() == null || bestMoveSoFar.getMoveEvaluation().isGameOver()) {
                stop = true;
            }

            bestMoveSoFarLbl.setText(bestMoveSoFar.getMove().getAnnotation());
            moveEvaluationLbl.setText(bestMoveSoFar.getMoveEvaluation().getEval() + "");
            clearAllHashMaps();
        }

        System.out.println("\nminimax move: " + bestMoveSoFar);
        System.out.println("max depth reached: " + (currentDepth - 1));
        System.out.println("num of positions reached: " + positionsReached);
        System.out.println("num of leaves reached: " + leavesReached);
        System.out.println("num of branches pruned: " + branchesPruned);
        System.out.println("num of transpositions hits: " + transpositionHits);

        return bestMoveSoFar;
    }

    private void clearAllHashMaps() {
        HashManager.clearAll();
        System.out.println("CLEAR THE FREAKING HASH MAPS");
    }

    MinimaxMove minimaxRoot(Model model, int maxDepth) {
        isCompleteSearch = new AtomicBoolean(true);
        pool = Executors.newFixedThreadPool(numOfThreads);

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
            pool.execute(() -> {
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
        pool.shutdown();
        try {
            if (!pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                System.out.println("had to stop the thingy");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Evaluation minimax(MinimaxParameters parms) {
        positionsReached++;

        long hash = parms.model.getBoardHash().getFullHash();
        if (HashManager.TRANSPOSITIONS) {
            Evaluation transpositionEval = getTranspositionEval(parms, hash);
            if (transpositionEval != null)
                return transpositionEval;
        }

        if (Eval.isGameOver(parms.model) || isOvertimeWithFlex() || parms.currentDepth >= parms.maxDepth) {
            leavesReached++;
            Evaluation evaluation = Eval.getEvaluation(parms.model, parms.minimaxPlayerColor);
            evaluation.setEvaluationDepth(parms.currentDepth / 2);
            return evaluation;
        }

        Evaluation bestEval = executeMovesMinimax(parms);

        if (HashManager.TRANSPOSITIONS)
            transpositionsHashMap.put(hash, new Transposition(parms, bestEval));
        return bestEval;
    }

    private Evaluation executeMovesMinimax(MinimaxParameters parms) {
        Evaluation bestEval = null;
        ArrayList<Move> possibleMoves = parms.model.generateAllMoves();
        sortMoves(possibleMoves, true);
        for (Move move : possibleMoves) {

            parms.model.applyMove(move);

            Evaluation eval = minimax(parms.nextDepth());

            parms.model.undoMove();

//            move.setMoveEvaluation(eval);

            if (bestEval == null || eval.isGreaterThan(bestEval) == parms.isMax) {
                bestEval = eval;
            }

            if (parms.isMax) {
                parms.a = Math.max(parms.a, bestEval.getEval());
            } else {
                parms.b = Math.min(parms.b, bestEval.getEval());
            }
            if (parms.b <= parms.a || eval.isGameOver()) {
                branchesPruned++;
                break;
            }

        }
        return bestEval;
    }

    private Evaluation getTranspositionEval(MinimaxParameters parms, long hash) {
        if (transpositionsHashMap.containsKey(hash)) {
            Transposition transposition = transpositionsHashMap.get(hash);
            if (transposition.getMaxDepth() >= parms.maxDepth) {
                transpositionHits++;
                Evaluation eval = new Evaluation(transposition.getEvaluation());
                if (transposition.getPlayer() != parms.minimaxPlayerColor) {
                    eval.flipEval();
                }
                return eval;
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

    class MinimaxParameters {
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