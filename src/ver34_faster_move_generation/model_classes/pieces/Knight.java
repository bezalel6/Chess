package ver34_faster_move_generation.model_classes.pieces;

import ver34_faster_move_generation.Location;
import ver34_faster_move_generation.model_classes.Board;
import ver34_faster_move_generation.model_classes.moves.Move;

import java.util.ArrayList;

public class Knight extends Piece {
    private static final int[] combinations = new int[]{
            1, 2,
            1, -2,
            2, 1,
            2, -1,
            -1, 2,
            -1, -2,
            -2, 1,
            -2, -1
    };

    public Knight(Location loc, int pieceColor) {
        super(loc, pieceColor, KNIGHT, "N");
    }

    public Knight(Piece other) {
        this(other.getStartingLoc(), other.getPieceColor());
        setLoc(other.getLoc());
    }

    public static ArrayList<ArrayList<Move>> createKnightMoves(Location movingFrom, int player) {
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();
        ArrayList<ArrayList<Move>> ret = new ArrayList();

        for (int i = 0; i < combinations.length; i += 2) {
            Location loc = new Location(myR + combinations[i], myC + combinations[i + 1]);
            if (loc.isInBounds())
                ret.add(new ArrayList<>() {{
                    add(new Move(movingFrom, loc, player, KNIGHT));
                }});
        }
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves() {
        return createKnightMoves(getLoc(), getPieceColor());
    }
}
