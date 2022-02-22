package ver19_square_control.types;

import ver19_square_control.Board;
import ver19_square_control.Location;
import ver19_square_control.moves.Move;

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

    public Knight(Location loc, int pieceColor) {
        super(loc, pieceColor, KNIGHT, "N");
    }

    public Knight(Piece other) {
        super(other);
    }

    public static ArrayList<ArrayList<Move>> createKnightMoves(Piece piece, Board board) {
        Location pieceLoc = piece.getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        ArrayList<ArrayList<Move>> ret = new ArrayList();

        for (int i = 0; i < combinations.length; i += 2) {
            Location loc = new Location(myR + combinations[i], myC + combinations[i + 1]);
            addSingleMove(loc, piece, ret, board);
        }
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createKnightMoves(this, board);
    }
}
