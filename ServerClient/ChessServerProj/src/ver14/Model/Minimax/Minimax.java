package ver14.Model.Minimax;

import ver14.Model.Eval.Book;
import ver14.Model.Eval.Eval;
import ver14.Model.Model;
import ver14.Model.ModelMovesList;
import ver14.Model.MoveGenerator.GenerationSettings;
import ver14.Model.MoveGenerator.MoveGenerator;
import ver14.SharedClasses.Game.Evaluation.Evaluation;
import ver14.SharedClasses.Game.Moves.MinimaxMove;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.ThreadsUtil;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Minimax - represents my implementation of a multithreaded minimax algorithm.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Minimax {

    /**
     * The constant DEBUG_MINIMAX.
     */
    public final static String DEBUG_MINIMAX = "DEBUG_MINIMAX";
    private static final boolean USE_OPENING_BOOK = true;
    private static final int DEFAULT_FLEX = (int) TimeUnit.SECONDS.toMillis(0);
    private static final int MOVE_ORDERING_DEPTH_CUTOFF = 5;
    /**
     * if set to true the debugging ui will show up in the server while the minimax is running and a log will print
     */
    public static boolean SHOW_UI = false;
    /**
     * The constant LOG.
     */
    public static boolean LOG = false;
    /**
     * the number of threads the minimax's Thread Pool will run on
     */
    public static int NUMBER_OF_THREADS = 10;
    /**
     * the scan time in milliseconds
     */
    private final long scanTimeInMillis;
    /**
     * the scan time flexibility in milliseconds. the time a search can continue (only trying to complete a depth, not starting a new one) after timing out
     */
    private final long scanTimeFlexibility;
    /**
     * useful debugging information window. will show up if the server is ran with the DEBUG_MINIMAX parameter
     */
    private final MinimaxView minimaxUI;
    /**
     * records for cpu usage analysis
     */
    private final CpuUsages cpuUsageRecords;
    /**
     * the model for the search's starting position
     */
    private Model model;
    /**
     * before starting the search the time is being captured so the elapsed time could be measured
     */
    private ZonedDateTime minimaxStartedTime;


    //    region some statistics that are disabled for performance's sake
    private long positionsReached;
    private long leavesReached;
    private long branchesPruned;
    private long transpositionHits;
    //endregion

    /**
     * keeping track of the book's ability to generate moves. once a move isnt found in the book, we're out of theory. since the current line isnt on the book
     */
    private boolean stillTheory;
    /**
     * the thread pool for the multithreading minimax
     */
    private ExecutorService threadPool;
    /**
     * the best move found
     */
    private MinimaxMove bestMove;
    /**
     * this boolean is needed so the root can tell weather or not the move returned from the minimax is the result of a full search
     */
    private AtomicBoolean isCompleteSearch;
    /**
     * should record the cpu usages
     */
    private boolean recordCpuUsage = false;
    /**
     * an interrupting error
     */
    private MyError interrupt = null;


    /**
     * Instantiates a new Minimax.
     *
     * @param model            the model
     * @param scanTimeInMillis the scan time in millis
     */
    public Minimax(Model model, long scanTimeInMillis) {
        this(model, scanTimeInMillis, DEFAULT_FLEX);
    }


    /**
     * Instantiates a new Minimax.
     *
     * @param model            the model
     * @param scanTimeInMillis the scan time in millis
     * @param flexibility      the flexibility
     */
    public Minimax(Model model, long scanTimeInMillis, long flexibility) {
        this.model = model;
        this.scanTimeFlexibility = flexibility;
        this.scanTimeInMillis = scanTimeInMillis;
        stillTheory = true;
        minimaxUI = new MinimaxView(SHOW_UI, this);
        cpuUsageRecords = new CpuUsages();
    }

    private static synchronized void log(String str) {
        if (LOG)
            System.out.println("minimax->" + str);
    }

    /**
     * Gets elapsed milliseconds from the time the minimax started
     *
     * @return the elapsed
     */
    public long getElapsed() {
        return minimaxStartedTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS);
    }

    /**
     * Sets model.
     *
     * @param model the model
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Sets record cpu usage.
     *
     * @param recordCpuUsage the record cpu usage
     */
    public void setRecordCpuUsage(boolean recordCpuUsage) {
        this.recordCpuUsage = recordCpuUsage;
    }

    /**
     * End.
     */
    public void end() {
        minimaxUI.stop();
        if (threadPool != null)
            threadPool.shutdown();
        quietInterrupt();
    }

    /**
     * Quiet interrupt.
     */
    public void quietInterrupt() {
        interrupt(new MyError(new QuietInterrupt()));

    }

    /**
     * Interrupt.
     *
     * @param error the error to interrupt the search with
     */
    public void interrupt(MyError error) {
        this.interrupt = error;
    }

    /**
     * Gets positions reached.
     *
     * @return the positions reached
     */
    public long getPositionsReached() {
        return positionsReached;
    }

    /**
     * Sets num of threads.
     *
     * @param numOfThreads the num of threads
     */
    public void setNumOfThreads(int numOfThreads) {
        this.NUMBER_OF_THREADS = numOfThreads;
    }

    /**
     * looks for a book move to play
     *
     * @return the book move if one is found. null otherwise
     */
    private MinimaxMove getBookMove() {
        if (USE_OPENING_BOOK && stillTheory) {
            String bookMoveStr = Book.getBookMove(model);
            if (bookMoveStr != null) {
                ModelMovesList possibleMoves = MoveGenerator.generateMoves(model, GenerationSettings.ANNOTATE);
                possibleMoves.initAnnotation();
                Move m = possibleMoves.stream().filter(m1 -> StrUtils.clean(m1.getAnnotation()).equals(bookMoveStr)).findAny().orElse(null);
                if (m != null) {
                    return new MinimaxMove(m, Evaluation.book());
                } else {
                    System.out.println("book move not found");
                }
            }
            stillTheory = false;
        }
        return null;
    }

    public Move getBestMove() {
        return getBestMove(model.getCurrentPlayer());
    }

    /**
     * Gets best move using minimax.
     *
     * @param player the player color to search for
     * @return the best move the minimax found
     */
    public Move getBestMove(PlayerColor player) {
        minimaxUI.resetBestMoves();
        MinimaxMove minimaxMove = getBestMoveUsingMinimax(player);
        Move move = minimaxMove.getMove();
        minimaxUI.setBestMoveSoFar();
        minimaxUI.setChosenMove(move.getAnnotation());
        return move;
    }

    /**
     * Gets evaluation for the given color.
     *
     * @param evaluatingFor the evaluating for
     * @return the evaluation
     */
    public Evaluation getEvaluation(PlayerColor evaluatingFor) {
        return getBestMoveUsingMinimax(evaluatingFor).getMoveEvaluation();
    }

    private void initSearch(PlayerColor player) {
        log("Current eval:\n" + Eval.getEvaluation(model, player));
        cpuUsageRecords.clear();
        minimaxUI.setNumOfThreads(NUMBER_OF_THREADS);
        minimaxUI.startTime();
        minimaxStartedTime = ZonedDateTime.now();
    }


    /**
     * Check interrupt minimax move.
     * if the minimax was interrupted by an actual error, it is thrown. if it is interrupted by a QuietInterrupt, the search is stopped and the best move found so far is returned
     *
     * @param returnedMove  the move just returned from the minimax search
     * @param bestMoveSoFar the best move found so far. in the whole search
     * @return the minimax move to be stored as the move returned from the last search
     */
    MinimaxMove checkInterrupt(MinimaxMove returnedMove, MinimaxMove bestMoveSoFar) {
        if (interrupt != null) {
            System.out.println("interrupting minimux in check interrupt err = " + interrupt);

            if (!(interrupt.getCause() instanceof QuietInterrupt))
                throw interrupt;
            assert bestMoveSoFar != null;
            return bestMoveSoFar;
        }
        return returnedMove;
    }

    /**
     * Gets best move using minimax.
     *
     * @return the best move found using minimax
     */
    private MinimaxMove getBestMoveUsingMinimax(PlayerColor player) {
        initSearch(player);
        if (player == model.getCurrentPlayer()) {
            MinimaxMove bookMove = getBookMove();
            if (bookMove != null)
                return bookMove;
        }

        MinimaxMove bestMoveSoFar = null;

        int currentDepth = 1;
        boolean stop = false;

        try {
            while (getElapsed() < scanTimeInMillis && !stop) {
                log("\n--------Starting search on depth " + currentDepth + "--------------");

                minimaxUI.setCurrentDepth(currentDepth, getElapsed());

                MinimaxMove minimaxMove = minimaxRoot(model, currentDepth, player);
                minimaxMove = checkInterrupt(minimaxMove, bestMoveSoFar);

                if (recordCpuUsage) {
                    recordCpuUsage(currentDepth);
                }

                log("depth " + currentDepth + " move: " + minimaxMove);
                currentDepth++;

                if (bestMoveSoFar == null || isCompleteSearch.get() /*|| minimaxMove.isDeeperAndBetterThan(bestMoveSoFar)*/) {
                    bestMoveSoFar = minimaxMove;
                }
                assert bestMoveSoFar != null && bestMoveSoFar.getMoveEvaluation() != null;
                stop = interrupt != null || bestMoveSoFar.getMoveEvaluation().isGameOver();

                minimaxUI.update(bestMoveSoFar);
            }
        } finally {
            minimaxUI.stopTimer();
        }

        log("\nminimax move: " + bestMoveSoFar);
        log("max depth reached: " + (currentDepth - 1));

        return bestMoveSoFar;
    }

    private void recordCpuUsage(int depth) {
        new Thread(() -> {
            double usage = ThreadsUtil.sampleCpuUsage();
            cpuUsageRecords.add(usage, depth);
            minimaxUI.setCpuUsage(usage);
        }).start();
    }

    /**
     * the entry point for the minimax.
     *
     * @param model    - current game position
     * @param maxDepth - the maximum depth the minimax can reach
     * @return best move for the current player to move
     */
    private MinimaxMove minimaxRoot(Model model, int maxDepth, PlayerColor playerColor) {
        isCompleteSearch = new AtomicBoolean(true);
        threadPool = new ForkJoinPool(NUMBER_OF_THREADS);

        try {
            startMultithreaded(model, playerColor, maxDepth);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (interrupt != null)
            throw interrupt;

        return bestMove;
    }

    /**
     * starts a multithreaded minimax search
     *
     * @param model              - current game position
     * @param minimaxPlayerColor - current player to move
     * @param maxDepth           - the maximum depth the minimax can reach
     */
    private void startMultithreaded(Model model, PlayerColor minimaxPlayerColor, int maxDepth) throws InterruptedException {
        ArrayList<Move> possibleMoves = model.generateAllMoves();

        AtomicReference<MinimaxMove> atomicBestMove = new AtomicReference<>(null);
        threadPool.execute(() -> {
            possibleMoves.stream().parallel().forEach(move -> {
                Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
                    System.out.println("inside threadpool " + MyError.errToString(e));
                    if (interrupt == null)
                        interrupt = new MyError(e);
                });
                if (!isOvertime() && interrupt == null) {
                    Model modelCopy = new Model(model);
                    modelCopy.applyMove(move);

                    Evaluation eval = minimax(new MinimaxParameters(modelCopy, false, maxDepth, minimaxPlayerColor));

                    move.setMoveEvaluation(eval);

                    synchronized (atomicBestMove) {
                        if (atomicBestMove.get() == null || eval.isGreaterThan(atomicBestMove.get().getMoveEvaluation())) {
                            atomicBestMove.set(new MinimaxMove(move, eval));
                        }
                    }
                    log("move: %s eval: %s".formatted(move, eval.getEval()));
                } else {
                    isCompleteSearch.set(false);
                }
            });

        });
        threadPool.shutdown();
        if (!threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
            throw new Error("thread pool error");
        }

        bestMove = atomicBestMove.get();
    }

    /**
     * makes every possible move and returns the best evaluation found for the current search player
     * (if is on a max layer, the current search player is the player who called the minimax. if it isn't, it's his opponent)
     *
     * @param parms the parms
     * @return the evaluation
     */
    private Evaluation minimax(MinimaxParameters parms) {

        //if the search has to stop for some reason
        if (interrupt != null) {
            throw interrupt;
        }
        //stop condition. if the minimax ran out of time, reached its maximum depth, or has reached a game over
        if (isOvertime() || parms.currentDepth >= parms.maxDepth || Eval.isGameOver(parms.model)) {
            Evaluation evaluation = Eval.getEvaluation(parms.model, parms.minimaxPlayerColor);
            evaluation.setEvaluationDepth(parms.currentDepth);
            return evaluation;
        }
        //the best evaluation found so far in this node
        Evaluation bestEval = null;
        ArrayList<Move> possibleMoves = MoveGenerator.generateMoves(parms.model, GenerationSettings.LEGALIZE);

        //at lower depths, sorting the moves is beneficial for the alpha-beta pruning
        if (parms.currentDepth < MOVE_ORDERING_DEPTH_CUTOFF)
            sortMoves(possibleMoves, true);

        for (int i = 0, possibleMovesSize = possibleMoves.size(); i < possibleMovesSize; i++) {
            Move move = possibleMoves.get(i);

            //again, stopping in case of an 'emergency'
            if (interrupt != null) {
                throw interrupt;
            }
            //making the current move
            parms.model.applyMove(move);

            //saving the resulting evaluation
            Evaluation eval = minimax(parms.nextDepth());

            //undoing the move
            parms.model.undoMove(move);

            //if the evaluation for this node isnt initialized or is the evaluation better for the current layer
            if (bestEval == null || eval.isGreaterThan(bestEval) == parms.isMax) {
                bestEval = eval;
            }

            //            alpha beta pruning
            if (parms.prune(bestEval)) {
                break;
            }

        }
        return bestEval;
    }

    /**
     * Gets cpu usage records.
     *
     * @return the cpu usage records
     */
    public CpuUsages getCpuUsageRecords() {
        return cpuUsageRecords;
    }

    private void sortMoves(ArrayList<Move> list, boolean isMax) {
        Collections.sort(list);
        if (isMax)
            Collections.reverse(list);
    }


    private boolean isOvertime() {
        return getElapsed() > scanTimeInMillis + scanTimeFlexibility;
    }

    /**
     * The type Cpu usages. (i should've made an object that has a depth and a usage but i dont have time to change this. consider this my official t *
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class CpuUsages {
        private final ArrayList<Double> usages;
        private final ArrayList<Integer> depths;

        /**
         * Instantiates a new Cpu usages.
         */
        public CpuUsages() {
            depths = new ArrayList<>();
            usages = new ArrayList<>();
        }

        /**
         * Add.
         *
         * @param usage the usage
         * @param depth the depth
         */
        public void add(double usage, int depth) {
            usages.add(usage);
            depths.add(depth);
        }

        /**
         * Gets usages.
         *
         * @return the usages
         */
        public ArrayList<Double> getUsages() {
            return usages;
        }

        /**
         * Gets depths.
         *
         * @return the depths
         */
        public ArrayList<Integer> getDepths() {
            return depths;
        }

        /**
         * Clear.
         */
        public void clear() {
            usages.clear();
            depths.clear();
        }
    }
}