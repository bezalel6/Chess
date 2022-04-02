package ver14.Tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ver14.Model.AttackedSquares;
import ver14.Model.FEN;
import ver14.Model.Model;
import ver14.Model.Perft.Perft;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.moves.BasicMove;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Game.pieces.PieceType;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * The type Move generation test.
 */
public class MoveGenerationTest extends Tests {
    private static final int POSITIONS_COUNT_DEPTH = 7;
    private static final boolean PRINT_POSITIONS_MOVES = true;
    private static final boolean MULTITHREADING_POS = true;

    @Test(dataProvider = "attackedPositions")
    private void attacked(String name, String fen, Location attackedLoc, PlayerColor attacked, boolean isAttacked) {

        AttackedSquares attackedSquares = new AttackedSquares(model, attacked.getOpponent());
        attackedSquares.attack(PieceType.PAWN);
        attackedSquares.attackedSquares.prettyPrint();

        Assert.assertEquals(AttackedSquares.isAttacked(model, attackedLoc, attacked.getOpponent()), isAttacked);
//        model.printBoard();
//        AttackedSquares squares = new AttackedSquares(model, PlayerColor.BLACK);
//        squares.getAttackedSquares().prettyPrint();
    }


    @DataProvider(name = "attackedPositions")
    private Object[][] attackedPositions() {
        ArrayList<Object[]> list = new ArrayList<>();
//        list.add(new Object[]{"thing", "4k3/8/8/8/1p6/2K5/8/8 w - - 2 5", true});
//        list.add(new Object[]{"thing", "rnbq1b1r/ppppkppp/5P2/8/8/8/PPP1PPPP/RNBQKBNR b KQ - 0 4", true});
        list.add(new Object[]{"thing", "rnbq1bnr/ppppp1pp/4kp2/5P2/3P4/8/PPP1P1PP/RNBQKBNR w KQ - 1 4", Location.E6, PlayerColor.BLACK, true});

        return list.stream().toList().toArray(new Object[0][]);
    }

    @DataProvider(name = "perftPositions")
    private Object[][] perftPositions() {
        ArrayList<Object[]> list = new ArrayList<>();
        IntStream.range(0, POSITIONS_COUNT_DEPTH).forEach(i -> {
            list.add(new Object[]{(i + 1) + "", FEN.startingFen, i + 1});
        });
//        list.add(new Object[]{"castling", FEN.castling, 4});
//        list.add(new Object[]{"pawn check", "r3k2r/p1pppppp/8/8/1p1P4/2K5/PPP1PPPP/R6R w kq - 1 4", 5});
//        list.add(new Object[]{"another pawn check", "r3k2r/p1pppppp/8/8/1p1P4/2K5/PPP1PPPP/R6R w kq - 1 4", 5});
//        list.add(new Object[]{"another pawn check", "rnbq1bnr/ppp1pppp/2kp4/3P4/8/5N2/PPP1PPPP/RNBQKB1R b KQ - 1 4", 1});

        return list.toArray(new Object[0][]);
    }

    /**
     * Perft.
     * <p>
     * fen is loaded in the before method
     *
     * @param name  the name
     * @param fen   the fen
     * @param depth the depth
     */
    @Test(dataProvider = "perftPositions")
    private void perft(String name, String fen, int depth) {
        long time = 0;
        startTime();
        Perft myPerf = compareMoves(depth);
        time += stopTime();
        System.out.println(name + " Result: " + myPerf.shortStr() + " positions Time: " + time + " milliseconds");
    }

    private Perft compareMoves(int depth) {
        Perft myPerf = root(model, depth);
        Perft stockfishPerft = stockfish.perft(depth);
        Map<BasicMove, String> diff = myPerf.getDifference(stockfishPerft);
        try {
            Assert.assertEquals(myPerf.getSum(), stockfishPerft.getSum());
            Assert.assertEquals(myPerf, stockfishPerft);
        } catch (AssertionError e) {
            StringBuilder bldr = new StringBuilder("\n");
            diff.forEach((move, str) -> {
                bldr.append(str).append("\n\n");
            });
            throw new AssertionError(bldr.toString(), e);
        }
        return myPerf;
    }


    private Perft root(Model model, int depth) {
        ForkJoinPool threadPool = new ForkJoinPool(MULTITHREADING_POS ? numOfThreads : 1);
        ArrayList<Move> moves = model.generateAllMoves();
        AtomicReference<Perft> perftResults = new AtomicReference<>(new Perft(depth));
        threadPool.execute(() -> {
            moves.stream().parallel().forEach(move -> {
                long result = executePos(depth, new Model(model), move, move);
                perftResults.get().set(move, result);
            });
        });
        try {
            threadPool.shutdown();
            if (!threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                throw new Error();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return perftResults.get();
    }

    private long countPositions(int depth, Model model, BasicMove root) {
        if (depth <= 0)
            return 1;
        ArrayList<Move> moves = model.generateAllMoves();
        return (moves.stream().mapToLong(move -> executePos(depth, model, move, root)).sum());
    }

    private long executePos(int depth, Model model, Move move, BasicMove root) {
        model.applyMove(move);
        long res = countPositions(depth - 1, model, root);
        if (PRINT_POSITIONS_MOVES && depth == POSITIONS_COUNT_DEPTH)
            System.out.println(move.getBasicMoveAnnotation() + ": " + res);
        model.undoMove(move);
        return res;
    }


}
