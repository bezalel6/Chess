package ver14.Tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import ver14.Model.FEN;
import ver14.SharedClasses.Game.Location;

/*
 * FenTests
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * FenTests -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * FenTests -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class FenTests extends Tests {
    @Test
    private void test() {
        String fen = "rnbqkbnr/ppp1pppp/8/2Pp4/8/8/PP1PPPPP/RNBQKBNR w KQkq d6 0 1";

        FEN.loadFEN(fen, model);

        Assert.assertEquals(model.getEnPassantTargetLoc(), Location.D6);

        model.generateAllMoves();

        Assert.assertEquals(model.getEnPassantTargetLoc(), Location.D6);

        Assert.assertEquals(model.getEnPassantActualLoc(), Location.D5);

    }
}
