package ver18_icon_manager.types;

import ver18_icon_manager.Board;
import ver18_icon_manager.Location;
import ver18_icon_manager.moves.Move;

import java.util.ArrayList;

public class Knight extends Piece {
    private static int[] combinations = new int[]{
            1, 2,
            1, -2,
            2, 1,
            2, -1,
            -1, 2,
            -1, -2,
            -2, 1,
            -2, -1
    };

    public Knight(Location loc, int pieceColor, boolean hasMoved) {
        super(loc, pieceColor, KNIGHT, "N", hasMoved);
    }

    public Knight(Piece other) {
        super(other);
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        ArrayList<ArrayList<Move>> ret = new ArrayList();

        for (int i = 0; i < combinations.length; i += 2) {
            Location loc = new Location(myR + combinations[i], myC + combinations[i + 1]);
            if (!isInBounds(loc)) continue;
            ret.add(new ArrayList<>() {{
                add(new Move(pieceLoc, loc, false, board));
            }});
        }
        return ret;
    }
}
