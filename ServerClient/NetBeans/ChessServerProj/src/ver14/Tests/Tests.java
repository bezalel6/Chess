package ver14.Tests;

import org.testng.ITest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ver14.Game.Game;
import ver14.Model.AttackedSquares;
import ver14.Model.Bitboard;
import ver14.Model.FEN;
import ver14.Model.Minimax.Minimax;
import ver14.Model.Model;
import ver14.Players.PlayerAI.MyAi;
import ver14.Players.PlayerAI.Stockfish.Stockfish;
import ver14.Server;
import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Game.GameSetup.*;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Utils.ArrUtils;
import ver14.ThreadsUtil;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.stream.IntStream;


@Test
public class Tests implements ITest {
    protected static final int numOfThreads = ThreadsUtil.NUM_OF_THREADS;
    private static final boolean enableInput = false;
    private static final int attackedFensNum = 10;
    private static ZonedDateTime dateTime;
    protected final ThreadLocal<String> testName = new ThreadLocal<>();
    //endregion
    protected Model model;
    protected Stockfish stockfish;

    public static void main(String[] args) throws Exception {
//        System.out.println(0b1 ^ 0b0);
//        minimaxVsStockfish();
    }

    private static void minimaxVsStockfish() {
        Game.showGameView = true;
        Server server = new Server();
        server.runServer();
        MyAi ai = new MyAi(new AISettings(AiType.MyAi, TimeFormat.BULLET)) {
            int num = 0;

            {
                setAnswer(Question.QuestionType.REMATCH, Question.Answer.NO);
            }

            @Override
            public GameSettings getGameSettings(SyncedItems<?> joinableGames, SyncedItems<?> resumableGames) {
                return num++ != 0 ? null : new GameSettings(PlayerColor.WHITE, TimeFormat.BULLET, "rnb1k1nr/pppppppp/5q2/2b5/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1", new AISettings(AiType.Stockfish, TimeFormat.BULLET), GameType.CREATE_NEW);
            }
        };
        server.gameSetup(ai);
    }

    private static void a() {

    }

    private static void compareAttacks() {
        VoidCallback one = Tests::fastAttacked;
        VoidCallback two = Tests::normalAttacked;
        one.callback();
        one.callback();
        one.callback();
        one.callback();

        two.callback();
        two.callback();
        two.callback();
        two.callback();
        int size = 100000;
        int checkIn = size / 1000;
        long[] times = {0, 0};
        System.out.printf("running %s times....\n", size);
        IntStream.range(0, size).forEach(
                i -> {
                    long[] arr = compareTimes("new attacked squares", one, "old attacked squares", two);
                    for (int j = 0; j < arr.length; j++) {
                        times[j] += arr[j];
                    }
                    if (i % checkIn == 0) {
                        System.out.println(i);

                    }
                }
        );
        System.out.println(Arrays.toString(times));
        Arrays.stream(times).forEach(l -> System.out.println(l / size));
    }

    private static void fastAttacked() {
        rerunOnInput(fen -> {
            Model model = model(fen);
//            boolean b = FastAttackedSquares.isAttacked(model.getLogicBoard(), model.getKing(PlayerColor.WHITE), PlayerColor.BLACK);
//            System.out.println(b);
        }, ArrUtils.createList(FEN::rndFen, attackedFensNum).toArray(new String[0]));
    }

    private static void normalAttacked() {
        rerunOnInput(fen -> {
            Model model = model(fen);
            boolean b = (AttackedSquares.isAttacked(model, model.getKing(PlayerColor.WHITE), PlayerColor.BLACK));
//            System.out.println(b);
        }, ArrUtils.createList(FEN::rndFen, attackedFensNum).toArray(new String[0]));
    }

    private static long[] compareTimes(String v1Name, VoidCallback v1, String v2Name, VoidCallback v2) {
        startTime();
        v1.callback();
        long one = stopTime();
        startTime();
        v2.callback();
        long two = stopTime();
//        System.out.println((one > two ? v2Name : v1Name) + " is better");
//        System.out.printf("%s:  %s\n%s: %s\n%s-%s=%s\n\n", v1Name, one, v2Name, two, one, two, one - two);
        return new long[]{one, two};
    }

    private static void rerunOnInput(Callback<String> run, String... startingValues) {
        Scanner scanner = new Scanner(System.in);
        for (String str : startingValues) {
            run.callback(str);
        }
        if (!enableInput)
            return;
        String str;
        do {
            System.out.println("waiting for input");
            str = scanner.nextLine();
            run.callback(str);
        } while (str != null && !str.equals("q"));
    }

    public static Model model(String fen) {
        return new Model() {{
            setup(fen);
        }};
    }

    //
//    @Test(testName = "")
//    private static void positionsCountRoot(int depth) {
//
//        Assert.assertEquals(1, 2, "");
//    }
    public static void startTime() {
        dateTime = ZonedDateTime.now();
    }

    public static long stopTime() {
        assert dateTime != null;
        return dateTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS);
    }

    private static void attackedSquares() {
        String fen = null;
        Model model = new Model(fen);
        System.out.println("Model: \n");
        model.printBoard();
        System.out.println();
//        Bitboard attacked = AttackedSquares.getPieceAttacksFrom(PieceType.QUEEN, new Bitboard(Location.E4), PlayerColor.WHITE, model);
//        System.out.println(attacked.prettyBoard());
    }

    private static void minimaxThreadsTest() {
        minimaxThreadsTest(10);
    }

    private static void minimaxThreadsTest(int time) {
        Minimax minimax = new Minimax(null, time);
        minimax.setRecordCpuUsage(true);
        Minimax.LOG = false;
        for (int threads = 1; threads <= numOfThreads; threads++) {
            Model model = new Model();
            model.setup(null);
            minimax.setModel(model);
            minimax.setNumOfThreads(threads);
            Move move = minimax.getBestMove(PlayerColor.WHITE);

            Minimax.CpuUsages cpuUsage = minimax.getCpuUsageRecords();
            ArrayList<Double> usages = cpuUsage.getUsages();
            Collections.sort(usages);
            Collections.reverse(usages);
            assert usages.size() > 0;
            double avg = usages.stream().reduce(0.0, Double::sum) / usages.size();
            double max = usages.get(0);
            double padding = 15;
            int index = 0;
            int numOfMatchesForConsistent = 2;
            int numFound = 0;
            double consistentMax = -1;
            while (index + 1 < usages.size() - 1) {
                double checking = usages.get(index++);
                double next = usages.get(index);
                if (next > checking - padding && next < checking + padding) {
                    numFound++;
                    if (numFound >= numOfMatchesForConsistent) {
                        consistentMax = checking;
                        break;
                    }
                }
            }
            System.out.println("Minimax = \n" + move);
            System.out.printf("\n%d:%d threads\navg: %f max: %f consistent max: %s\n", threads, numOfThreads, avg, max, consistentMax != -1 ? consistentMax : "n/a");
            System.out.println(usages);
        }
        minimax.end();

    }

    @Test
    private void genExample() {
        model.setup("8/8/1P6/8/8/3P4/6P1/8 w - - 0 1");
        Bitboard bb = model.getPlayersPieces(PlayerColor.WHITE).getBB(PieceType.PAWN);
        System.out.println(bb);
        bb.prettyPrint();
        Bitboard a = AttackedSquares.getAttackedSquares(model, PlayerColor.WHITE);
        System.out.println(a);
        a.prettyPrint();
//        Assert.assertEquals(new Bitboard().toString(), StrUtils.repeat((i, b) -> "0", 64));

    }

    @BeforeMethod
    protected void BeforeMethod(Method method, Object[] testData) {
//        testName.set("b4 method of" + method.getName() + " data: " + testData);
        Object name = ArrUtils.exists(testData, 0, 0);
        if (name == null)
            name = "";
        testName.set(method.getName() + "_" + name);
        String fen = (String) ArrUtils.exists(testData, 1);
        if (fen != null)
            FEN.assertFen(fen);
        model = model(fen);
        stockfish = new Stockfish(fen);
    }

    @Test
    private void ArrUtilsTest() {
        Object[][] objs = new Object[][]{{new Object()}};
        System.out.println(ArrUtils.exists(objs, 0, 0).getClass());
    }

    @AfterMethod
    private void after() {
        stockfish.stopEngine();
    }

    @Override
    public String getTestName() {
        return testName.get();
    }


}
