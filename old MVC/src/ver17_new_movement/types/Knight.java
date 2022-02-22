package ver17_new_movement.types;

import ver17_new_movement.Board;
import ver17_new_movement.Location;
import ver17_new_movement.moves.Move;

import java.util.ArrayList;
import java.util.Arrays;

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
            ArrayList<Move> t = new ArrayList<>();
            Location loc = new Location(myR + combinations[i], myC + combinations[i + 1]);
            if (!isInBounds(loc)) continue;
            t.add(new Move(pieceLoc, loc, false, board));
            ret.add(t);
        }
        return ret;
    }
}
