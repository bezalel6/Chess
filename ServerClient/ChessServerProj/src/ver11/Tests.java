package ver11;

import ver11.Model.*;
import ver11.Model.Eval.Eval;
import ver11.Model.minimax.Minimax;
import ver11.SharedClasses.Location;
import ver11.SharedClasses.PlayerColor;
import ver11.SharedClasses.evaluation.Evaluation;
import ver11.SharedClasses.moves.Move;
import ver11.SharedClasses.moves.MovesList;
import ver11.SharedClasses.pieces.PieceType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class Tests {
    private static final int POSITIONS_COUNT_DEPTH = 6;
    private static final boolean PRINT_POSITIONS_MOVES = false;
    private static final boolean MULTITHREADING_POS = true;
    private final static AtomicBoolean threw = new AtomicBoolean(false);
    static ServerSocket serverSocket = null;
    static ExecutorService pool = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws Exception {
    }

    public static void testSocket() throws Exception {
        serverSocket = new ServerSocket(1234);
        new Thread(Tests::startAccepting).start();
        for (int i = 1; i <= 10 && !threw.get(); i++) {
            createClientInNewThread("127.0.0.1", 1234, "Id = " + i + "");
//            Thread.sleep(6);
        }
    }

    private static void startAccepting() {
        int num = 0;
        while (true) {
            try {
                String[] ids = new String[2];
                IntStream.range(0, 2).forEach(i -> {
                    try {
                        ids[i] = acceptSocket();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                num++;
                System.out.println(Arrays.toString(ids));
                if (!Objects.equals(ids[0], ids[1])) {
                    throw new Exception("mismatch after " + num + " double clients");
                }
                System.out.println("accepted " + num + " clients");
            } catch (Exception e) {
                e.printStackTrace();
                threw.set(true);
                break;
            }

        }
    }

    private static void createClientInNewThread(String url, int port, String id) {
        pool.execute(() -> {
            IntStream.range(0, 2).forEach(i -> {
                try {
                    Socket socket = new Socket(url, port);
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeUTF(id);
                    System.out.println("created client");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }


    private static String acceptSocket() throws IOException {
        Socket socket = serverSocket.accept();
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        return inputStream.readUTF();
    }

    //todo check if Math.abs(-1).... is faster then if/switch/opp hash map
    private static void mathVsIf() {
        enum e {
            a, b, c
        }
    }

    private static void minimaxVsStockfish() {
        Model model = new Model();
        boolean m = new Random().nextBoolean();
        while (!Eval.isGameOver(model)) {
            Move move;
            if (m) {

            } else {

            }
            m = !m;
        }
    }


    private static void ray() {
        String fen = "3r4/8/1Rbk4/2q5/2N5/Q7/8/3R4 w - - 0 1";
        Model model = new Model(fen);
        Attack check = AttackedSquares.getCheck(model, PlayerColor.WHITE);
        check.prettyPrint();
    }

    private static void pins() {
        String fen = "3r4/8/1Rbk4/2q5/2N5/Q7/8/3R4 w - - 0 1";
        Model model = new Model(fen);
        System.out.println("Model: \n");
        model.printBoard();
        Pins pins = AttackedSquares.getPins(model, PlayerColor.WHITE);
        pins.prettyPrint();

    }

    private static void rayVsMoveLegalization() {
        Model model = new Model();
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

    private static void moveGeneration() {
        String fen = null;
        Model model = new Model(fen);
        model.generateAllMoves();
//        ModelMovesList moves = MoveGenerator.generateMoves(model);
//        System.out.println(moves);
//        moves.prettyPrint();
    }

    private static void shiftVsLookupTime() {
        int numOfTries = 100000000;
        long time = 0;
        for (int i = 0; i < numOfTries; i++) {
            long millis = System.currentTimeMillis();
            Bitboard.getLong(Math.abs(new Random().nextInt(64)));
            time += System.currentTimeMillis() - millis;

        }
        System.out.println("lookup took " + time);
        time = 0;
        for (int i = 0; i < numOfTries; i++) {
            long millis = System.currentTimeMillis();
//            Bitboard.getSlowLong(Math.abs(new Random().nextInt(64)));

            time += System.currentTimeMillis() - millis;
        }

        System.out.println("shift took " + time);
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

    private static void minimax() {
        Model model = new Model();
        Minimax minimax = new Minimax(model, 10);
        Move move = minimax.getBestMove();
        System.out.println("Minimax = \n" + move);
        model.makeMove(move);
        System.out.println("Next move = \n" + minimax.getBestMove());
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
        int size = moves.size();
        int num;
        if (isRoot) {
            num = IntStream.range(0, size).parallel().map(i -> executePos(depth, new Model(model), moves.get(i))).sum();
        } else {
            num = moves.stream().mapToInt(move -> executePos(depth, model, move)).sum();
        }
        return num;
    }

    private static int executePos(int depth, Model model, Move move) {
        model.applyMove(move);
        int res = countPositions(depth - 1, model, false);
        if (PRINT_POSITIONS_MOVES && depth == POSITIONS_COUNT_DEPTH)
            System.out.println(move.getMovingFrom().toString() + "" + move.getMovingTo().toString() + ": " + res);
        model.undoMove();
        return res;
    }

    public static void printNumOfPositions() {
        Model model = new Model();
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