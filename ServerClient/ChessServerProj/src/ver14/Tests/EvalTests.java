package ver14.Tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ver14.Model.Eval.Eval;
import ver14.Model.Eval.Tables;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.moves.BasicMove;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Game.pieces.PieceType;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class EvalTests extends Tests {

    public void checkColors() {
        for (PieceType pieceType : PieceType.PIECE_TYPES) {
            Tables.PieceTable table = Tables.getPieceTable(pieceType);
            for (Location loc : Location.ALL_LOCS) {

                Assert.assertEquals(table.getValue(1, PlayerColor.WHITE, loc), table.getValue(1, PlayerColor.BLACK, loc.flip()));
            }
        }
    }

    @DataProvider(name = "repetitions")
    public Object[][] moveSequences() {

        return new Object[][]{{crateRepeatingSequence(20, new BasicMove(Location.B1, Location.C3), new BasicMove(Location.B8, Location.C6)), true}, {new BasicMove[]{new BasicMove(Location.E2, Location.E4), new BasicMove(Location.E7, Location.E5), new BasicMove(Location.G1, Location.F3), new BasicMove(Location.B8, Location.C6), new BasicMove(Location.F3, Location.G1), new BasicMove(Location.C6, Location.B8), new BasicMove(Location.G1, Location.F3)}, false}};
    }

    private BasicMove[] crateRepeatingSequence(int len, BasicMove white, BasicMove black) {
        BasicMove[] moves = new BasicMove[len];
        for (int i = 0; i < len; i++) {
            BasicMove basicMove = i % 2 == 0 ? white : black;
            moves[i] = basicMove.cp();
            basicMove.flip();
        }
        return moves;
    }

    @BeforeMethod
    @Override
    protected void BeforeMethod(Method method, Object[] testData) {
        super.BeforeMethod(method, new Object[0]);
    }

    @Test(dataProvider = "repetitions")
    public void testRepetition(BasicMove[] moves, boolean isRepetition) {
        AtomicBoolean ret = new AtomicBoolean(false);
        IntStream.range(0, moves.length).forEach(i -> {
            if (ret.get())
                return;

            Move move = model.findMove(moves[i]);
            model.makeMove(move);
            System.out.println("move: " + (i + 1));
            model.printBoard();
            Eval.PRINT_REP_LIST = true;
            if (Eval.isGameOver(model)) ret.set(true);
            Eval.PRINT_REP_LIST = false;
        });
        Assert.assertEquals(ret.get(), isRepetition);
    }

}
