package ver13.Tests;

import ver13.Model.*;
import ver13.Model.Eval.Eval;
import ver13.Model.MoveGenerator.MoveGenerator;
import ver13.Model.minimax.Minimax;
import ver13.Server;
import ver13.SharedClasses.Location;
import ver13.SharedClasses.PlayerColor;
import ver13.SharedClasses.evaluation.Evaluation;
import ver13.SharedClasses.moves.Move;
import ver13.SharedClasses.moves.MovesList;
import ver13.SharedClasses.pieces.PieceType;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Tests {
    private static final int POSITIONS_COUNT_DEPTH = 5;
    private static final boolean PRINT_POSITIONS_MOVES = false;
    private static final boolean MULTITHREADING_POS = true;
    //    private static final int numOfThreads = 6;
    private static final int numOfThreads = Runtime.getRuntime().availableProcessors();

    private static ZonedDateTime dateTime;

    public static void main(String[] args) throws Exception {
//        printNumOfPositions();
        minimax(2);
//        isInCheck();
    }

    private static void minimax(int time) {
        for (int threads = 1; threads <= numOfThreads; threads++) {
            Model model = new Model();
            model.setup(null);
            Minimax minimax = new Minimax(model, time);
            minimax.setLog(false);
            minimax.setNumOfThreads(threads);
            minimax.setRecordCpuUsage(true);
            Move move = minimax.getBestMove();

            ArrayList<Double> cpuUsage = minimax.getCpuUsageRecords();
            Collections.sort(cpuUsage);
            Collections.reverse(cpuUsage);
            System.out.println("Minimax = \n" + move);
            assert cpuUsage.size() > 0;
            double avg = cpuUsage.stream().reduce((double) 0, Double::sum) / cpuUsage.size();
            double max = cpuUsage.get(0);
            double padding = 15;
            int index = 0;
            int numOfMatchesForConsistent = 2;
            int numFound = 0;
            double consistentMax = -1;
            while (index + 1 < cpuUsage.size() - 1) {
                double checking = cpuUsage.get(index++);
                double next = cpuUsage.get(index);
                if (next > checking - padding && next < checking + padding) {
                    numFound++;
                    if (numFound >= numOfMatchesForConsistent) {
                        consistentMax = checking;
                        break;
                    }
                }
            }
            System.out.printf("\n%d:%d threads\navg: %f max: %f consistent max: %s\n", threads, numOfThreads, avg, max, consistentMax != -1 ? consistentMax : "n/a");
            System.out.println(cpuUsage);
            minimax.end();
        }

    }

    private static void minimax() {
        minimax(10);
    }

    private static void minimaxVsStockfish() {
        Server server = new Server();
        Server.VS_STOCKFISH = true;
        server.runServer();
    }

    private static Model create() {
        return new Model() {{
            setup(null);
        }};
    }

    private static void isInCheck() {
        Model model = new Model();

        model.setup("1n2kbnr/pppppppp/3q4/8/r2K1b2/8/PP1PPPPP/RNBQ1BNR w k - 0 1");
//        model.setup(null);
        ;
    }

    private static void rayVsMoveLegalization() {
        Model model = new Model();
        model.setup(null);
        MovesList moves = MoveGenerator.generateMoves(model);
        long time = 0;
        int numOfTries = 100000;
        for (int i = 0; i < numOfTries; i++) {
            long millis = System.currentTimeMillis();
            for (Move move : moves) {
                model.applyMove(move);
                model.isInCheck();
                model.undoMove();
            }
            time += System.currentTimeMillis() - millis;
        }

        System.out.println("Legalization took " + time);
        time = 0;
        for (int i = 0; i < numOfTries; i++) {
            long millis = System.currentTimeMillis();
            AttackedSquares.getPins(model, PlayerColor.WHITE);
            time += System.currentTimeMillis() - millis;
        }
        System.out.println("Ray took " + time);

    }

    private static void ray() {
        String fen = "3r4/8/1Rbk4/2q5/2N5/Q7/8/3R4 w - - 0 1";
        Model model = new Model(fen);
        Attack check = AttackedSquares.getCheck(model, PlayerColor.WHITE);
        check.prettyPrint();
    }

    public static void startTime() {
        dateTime = ZonedDateTime.now();
    }

    public static long stopTime() {
        assert dateTime != null;
        return dateTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS);
    }

    //region
    //todo check if Math.abs(-1).... is faster then if/switch/opp hash map
    private static void mathVsIf() {
        enum e {
            a, b, c
        }
    }

    private static void pins() {
        String fen = "3r4/8/1Rbk4/2q5/2N5/Q7/8/3R4 w - - 0 1";
        Model model = new Model(fen);
        System.out.println("Model: \n");
        model.printBoard();
        Pins pins = AttackedSquares.getPins(model, PlayerColor.WHITE);
        pins.prettyPrint();

    }

    private static void moveGeneration() {
        String fen = null;
        Model model = new Model(fen);
        model.generateAllMoves();
//        ModelMovesList moves = MoveGenerator.generateMoves(model);
//        System.out.println(moves);
//        moves.prettyPrint();
    }

    private static void testSetLocsTime() {
        int numOfTries = 1000000;
        long time = 0;
        for (int i = 0; i < numOfTries; i++) {
            long millis = System.currentTimeMillis();
            Bitboard bitboard = new Bitboard(Math.abs(new Random().nextLong()));
            ArrayList<Location> setLocs = new ArrayList<>();
            int position = 1;
            long num = bitboard.getBitBoard();
            while (num != 0) {
                if ((num & 1) != 0) {
                    Location loc = Location.getLoc(position - 1);
                    assert loc != null;
                    setLocs.add(loc);
                }
                position++;
                num = num >>> 1;
            }
            time += System.currentTimeMillis() - millis;

        }
        System.out.println("shifting took " + time);
        time = 0;
        for (int i = 0; i < numOfTries; i++) {
            long millis = System.currentTimeMillis();
            Bitboard bitboard = new Bitboard(Math.abs(new Random().nextLong()));
            ArrayList<Location> setLocs = new ArrayList<>();
            for (Location loc : Location.ALL_LOCS) {
                if (bitboard.isSet(loc))
                    setLocs.add(loc);
            }
            time += System.currentTimeMillis() - millis;
        }

        System.out.println("is set took " + time);
    }

    private static void fen() {
        Model model = new Model();
        System.out.println("Fen = " + FEN.generateFEN(model));
//        FEN.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", null);
    }

    private static void threefold() {
        Model model = new Model();
        String[] pgn = {"Nc3", "Nc6", "Nb1", "Nb8", "Nc3", "Nc6", "Nb1", "Nb8"};
        int index = 0;
        while (!Eval.isGameOver(model)) {
            var moves = model.generateAllMoves();
            int finalIndex = index;
            Move applying = moves.stream()
                    .filter(move ->
                            move.getAnnotation().startsWith(pgn[finalIndex]))
                    .findAny()
                    .orElse(null);
            index++;
            if (applying == null) {
                System.out.println("didnt find. moves = " + moves);
                assert false;
            }
            System.out.println("Move: " + applying + " Model:\n");
            model.printBoard();

            model.applyMove(applying);

            if (applying.getThreefoldStatus() == Move.ThreefoldStatus.CAN_CLAIM) {
                System.out.println("Found threefold!");
                break;
            }
        }
        System.out.println(Eval.getEvaluation(model));
    }

    private static void evaluation() {
        Model model = new Model();
        Evaluation evaluation = Eval.getEvaluation(model);
        System.out.println("Evaluation = " + evaluation);
    }

    private static void fiftyMoves() {
        Model model = new Model();
        int i = 0;
        while (!Eval.isGameOver(model)) {
            Move move = model.generateAllMoves()
                    .stream()
                    .filter(Move::isReversible)
                    .findAny()
                    .orElse(null);
            if (move == null) {
                System.out.println("Couldnt find a reversible move.");
                break;
            }
            System.out.println("Move " + (++i) + " = " + move);
            model.applyMove(move);

        }
        System.out.println(model);
    }

    private static void attackedSquares() {
        String fen = null;
        Model model = new Model(fen);
        System.out.println("Model: \n");
        model.printBoard();
        System.out.println();
        Bitboard attacked = AttackedSquares.getPieceAttacksFrom(PieceType.QUEEN, new Bitboard(Location.E4), PlayerColor.WHITE, model);
        System.out.println(attacked.prettyBoard());
    }

    //endregion
    private static int[] testPositions(int depth, Model model) {
        ZonedDateTime minimaxStartedTime = ZonedDateTime.now();
        int res = countPositions(depth, model, MULTITHREADING_POS);
        int time = (int) minimaxStartedTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS);
        return new int[]{res, time};
    }

    public static int countPositions(int depth, Model model, boolean isRoot) {
        if (depth == 0)
            return 1;
        ArrayList<Move> moves = model.generateAllMoves();
        AtomicInteger num = new AtomicInteger();

        if (isRoot) {
//            ForkJoinPool threadPool = ForkJoinPool.commonPool();
            ForkJoinPool threadPool = new ForkJoinPool(numOfThreads);
            threadPool.execute(() -> {
                num.set(moves.stream().parallel().mapToInt(move -> executePos(depth, new Model(model), move)).sum());
            });
            try {
                threadPool.shutdown();
                if (!threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                    System.out.println("f");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            num.set(moves.stream().mapToInt(move -> executePos(depth, model, move)).sum());
        }
        return num.get();
    }

    private static int executePos(int depth, Model model, Move move) {
        model.applyMove(move);
        int res = countPositions(depth - 1, model, false);
        if (PRINT_POSITIONS_MOVES && depth == POSITIONS_COUNT_DEPTH)
            System.out.println(move.getBasicMoveAnnotation() + ": " + res);
        model.undoMove();
        return res;
    }

    public static void printNumOfPositions() {

        Model model = new Model();
        model.setup(null);
//        new Thread(() -> {
        int numOfVar = 1;
        for (int depth = 1; depth <= POSITIONS_COUNT_DEPTH; depth++) {
            int res = 0, time = 0;
            for (int j = 0; j < numOfVar; j++) {
                int[] arr = testPositions(depth, model);
                res = arr[0];
                time += arr[1];
            }
            time /= numOfVar;
            System.out.println("Depth: " + depth + " Result: " + res + " positions Time: " + time + " milliseconds");
        }
//        }).start();

    }
}
