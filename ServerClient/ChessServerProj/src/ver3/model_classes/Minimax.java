package ver3.model_classes;

import ver3.model_classes.eval_classes.Book;
import ver3.model_classes.eval_classes.Eval;
import ver3.model_classes.eval_classes.Evaluation;
import ver3.model_classes.moves.BasicMove;
import ver3.model_classes.moves.MinimaxMove;
import ver3.model_classes.moves.Move;

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

public class Minimax {
    public static final ConcurrentHashMap<Long, Transposition> transpositionsHashMap = new ConcurrentHashMap<Long, Transposition>();
    static final boolean USE_OPENING_BOOK = true;
    private static final int SCAN_TIME_FLEXIBILITY = 10;
    private final Model model;
    private ZonedDateTime minimaxStartedTime;
    private long positionsReached;
    private long leavesReached;
    private long branchesPruned;
    private long transpositionHits;
    private boolean stillTheory;
    private ExecutorService pool;
    private int numOfThreads = 2;
    private int scanTime;

    public Minimax(Model model, int scanTime) {
        this.model = model;
        stillTheory = true;
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
        return minimaxStartedTime.until(ZonedDateTime.now(), ChronoUnit.SECONDS);
    }

    public Move getBestMoveUsingStockfish() {
        String m = new Stockfish().getBestMove(model.getLogicBoard().getFenStr(), 1000 + new Random().nextInt(1000));
        BasicMove basicMove = new BasicMove(Location.getLoc(m.substring(0, 2)), Location.getLoc(m.substring(2)));
        return model.getLogicBoard().findMove(basicMove);
    }

    private MinimaxMove getBookMove() {
        if (USE_OPENING_BOOK && stillTheory) {
            String bookMove = Book.getBookMove(model.getLogicBoard());
            if (bookMove != null) {
                ArrayList<Move> possibleMoves = model.getLogicBoard().generateAllMoves();
                for (Move move : possibleMoves) {
                    if (move.getAnnotation().equals(bookMove))
                        return new MinimaxMove(move, new Evaluation(), 0);
                }
            }
            stillTheory = false;
        }
        return null;
    }// ============================ for minimax ===========================

    private MinimaxMove getBestMoveUsingMinimax() {
        MinimaxMove bookMove = getBookMove();
        if (bookMove != null)
            return bookMove;
        System.out.println("Current eval:\n" + model.getLogicBoard().getEvaluation());
        MinimaxMove bestMoveSoFar = null;
        positionsReached = -1;
        leavesReached = 0;
        branchesPruned = 0;
        transpositionHits = 0;
        int i = 1;
        boolean stop = false;
        while (getElapsedSeconds() < scanTime && !stop) {
            positionsReached = -1;
            leavesReached = 0;

            String s = "-------------";
            System.out.println("\n" + s + "Starting search on depth " + i + s);

            MinimaxMove minimaxMove = minimaxRoot(model.getLogicBoard(), i);
            System.out.println("depth " + i + " move: " + minimaxMove);
            i++;

            if (bestMoveSoFar == null || minimaxMove.isCompleteSearch() || minimaxMove.isDeeperAndBetterThan(bestMoveSoFar)) {
                bestMoveSoFar = minimaxMove;
            }
            assert bestMoveSoFar.getMove() != null;
            if (bestMoveSoFar.getMoveEvaluation() == null || bestMoveSoFar.getMoveEvaluation().isGameOver()) {
                stop = true;
            }
            clearAllHashMaps();
        }

        System.out.println("\nminimax move: " + bestMoveSoFar);
        System.out.println("max depth reached: " + (i - 1));
        System.out.println("num of positions reached: " + positionsReached);
        System.out.println("num of leaves reached: " + leavesReached);
        System.out.println("num of branches pruned: " + branchesPruned);
        System.out.println("num of transpositions hits: " + transpositionHits);

        return bestMoveSoFar;
    }

    private void clearAllHashMaps() {
        System.out.println("CLEAR THE FREAKING HASH MAPS");
    }

    MinimaxMove minimaxRoot(Board board, int maxDepth) {
        MinimaxMove bestMove;
        AtomicBoolean isCompleteSearch = new AtomicBoolean(true);
        pool = Executors.newFixedThreadPool(numOfThreads);

        startMultithreadedMinimax(board, board.getCurrentPlayer(), maxDepth);
        endMultithreadedMinimax();

        ArrayList<Move> possibleMoves = board.generateAllMoves();

        sortMoves(possibleMoves, true);
        bestMove = new MinimaxMove(possibleMoves.get(0), possibleMoves.get(0).getMoveEvaluation(), 0);
        bestMove.setCompleteSearch(isCompleteSearch.get());

        return bestMove;
    }

    private void startMultithreadedMinimax(Board board, Player minimaxPlayer, int maxDepth) {
        ArrayList<Move> possibleMoves = board.generateAllMoves();

        possibleMoves.forEach(move -> {
            pool.execute(() -> {
                if (!isOvertimeWithFlex()) {
                    Board board1 = new Board(board);
                    board1.applyMove(move);
                    Evaluation eval = minimax(new MinimaxParameters(board1, false, maxDepth, minimaxPlayer, move));
                    board1.undoMove();

                    move.setMoveEvaluation(eval);
                }
            });
        });

    }

    private void endMultithreadedMinimax() {
        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Evaluation minimax(MinimaxParameters parms) {
        positionsReached++;

        Eval boardEval = parms.board.getEval();

        long hash = parms.board.getBoardHash().getFullHash();
        Evaluation transpositionEval = getTranspositionEval(parms, hash);
        if (transpositionEval != null)
            return transpositionEval;

        if (boardEval.checkGameOver().isGameOver() || isOvertimeWithFlex() || parms.currentDepth >= parms.maxDepth) {
            leavesReached++;
            return boardEval.getEvaluation(parms.minimaxPlayer);
        }

        Evaluation bestEval = executeMovesMinimax(parms);

        transpositionsHashMap.put(hash, new Transposition(parms, bestEval));
        return bestEval;
    }

    private Evaluation executeMovesMinimax(MinimaxParameters parms) {
        Evaluation bestEval = new Evaluation(parms.isMax);
        ArrayList<Move> possibleMoves = parms.board.generateAllMoves();

        for (Move move : possibleMoves) {

            parms.board.applyMove(move);

            Evaluation eval = minimax(parms.nextDepth());

            parms.board.undoMove();

            move.setMoveEvaluation(eval);

            if (eval.isGreaterThan(bestEval) == parms.isMax) {
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
        return bestEval;
    }

    private Evaluation getTranspositionEval(MinimaxParameters parms, long hash) {
        if (transpositionsHashMap.containsKey(hash)) {
            Transposition transposition = transpositionsHashMap.get(hash);
            if (transposition.getMaxDepth() >= parms.maxDepth) {
                transpositionHits++;
                Evaluation eval = new Evaluation(transposition.getEvaluation());
                if (transposition.getPlayer() != parms.minimaxPlayer) {
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
        return getElapsedSeconds() > scanTime + SCAN_TIME_FLEXIBILITY;
    }

    class MinimaxParameters {
        private final Board board;
        private final boolean isMax;
        private final Player minimaxPlayer;
        private final int currentDepth, maxDepth;
        private final Move rootMove;
        private double a, b;

        public MinimaxParameters(Board board, boolean isMax, int maxDepth, Player minimaxPlayer, Move rootMove) {
            this(board, isMax, 1, maxDepth, minimaxPlayer, -Evaluation.WIN_EVAL, Evaluation.WIN_EVAL, rootMove);
        }

        public MinimaxParameters(Board board, boolean isMax, int currentDepth, int maxDepth, Player minimaxPlayer, double a, double b, Move rootMove) {
            this.board = board;
            this.isMax = isMax;
            this.maxDepth = maxDepth;
            this.minimaxPlayer = minimaxPlayer;
            this.currentDepth = currentDepth;
            this.a = a;
            this.b = b;
            this.rootMove = rootMove;

        }

        public Board getBoard() {
            return board;
        }

        public boolean isMax() {
            return isMax;
        }

        public Player getMinimaxPlayer() {
            return minimaxPlayer;
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
            return new MinimaxParameters(board, !isMax, currentDepth + 1, maxDepth, minimaxPlayer, a, b, rootMove);
        }
    }

    class StoreMinimax {
        private final Board board;
        private final Move rootMove;

        public StoreMinimax(MinimaxParameters parms) {
            this(parms.board, parms.rootMove);
        }

        public StoreMinimax(Board board, Move rootMove) {
            this.board = new Board(board);
            this.rootMove = rootMove;
        }

        public Board getBoard() {
            return board;
        }

        public Move getRootMove() {
            return rootMove;
        }
    }
}