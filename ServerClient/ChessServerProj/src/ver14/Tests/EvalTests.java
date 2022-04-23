package ver14.Tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ver14.Model.Eval.Eval;
import ver14.Model.Eval.Tables;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.Moves.BasicMove;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Utils.ArrUtils;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static ver14.SharedClasses.Game.Location.*;

/*
 * EvalTests
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * EvalTests -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * EvalTests -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

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
        return new Object[][]{{ArrUtils.concat(BasicMove.createBatch(E2, E4, E7, E5), crateRepeatingSequence(20, new BasicMove(B1, C3), new BasicMove(B8, C6))), 11}, {BasicMove.createBatch(E2, E4, E7, E5, G1, F3, B8, C6, F3, G1, C6, B8, G1, F3), -1}};
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
    public void testRepetition(BasicMove[] moves, int findInPly) {
        AtomicInteger foundInDepth = new AtomicInteger(-1);
        IntStream.range(0, moves.length).forEach(i -> {
            if (foundInDepth.get() != -1)
                return;

            Move move = model.findMove(moves[i]);
            model.makeMove(move);
            System.out.println("move: " + (i + 1));
            model.printBoard();
            Eval.PRINT_REP_LIST = true;
            if (Eval.isGameOver(model)) {
                foundInDepth.set(i + 1);
            }
            Eval.PRINT_REP_LIST = false;
        });
        Assert.assertEquals(foundInDepth.get(), findInPly);
    }

}
