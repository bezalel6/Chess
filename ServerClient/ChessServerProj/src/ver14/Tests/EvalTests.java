package ver14.Tests;

import org.testng.Assert;
import ver14.Model.Eval.Tables;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.pieces.PieceType;

public class EvalTests extends Tests {

    public void checkColors() {
        for (PieceType pieceType : PieceType.PIECE_TYPES) {
            Tables.PieceTable table = Tables.getPieceTable(pieceType);
            for (Location loc : Location.ALL_LOCS) {

                Assert.assertEquals(table.getValue(1, PlayerColor.WHITE, loc), table.getValue(1, PlayerColor.BLACK, loc.flip()));
            }
        }
    }

}
