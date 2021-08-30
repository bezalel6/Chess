package ver19_square_control.types;

import ver19_square_control.Board;
import ver19_square_control.Location;
import ver19_square_control.moves.Move;

import java.util.ArrayList;

public class Bishop extends Piece {
    public static double worth = 3.2;
    private static int[] combinations = new int[]{
            1, 1,
            -1, -1,
            1, -1,
            -1, 1
    };

    public Bishop(Location loc, int pieceColor) {
        super(loc, pieceColor, BISHOP, "B");
    }

    public Bishop(Piece other) {
        super(other);
    }

    public static ArrayList<ArrayList<Move>> createBishopMoves(Piece piece, Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        Location myLoc = piece.getLoc();
        int myR = myLoc.getRow();
        int myC = myLoc.getCol();

        for (int j = 0; j < 4; j++) {
            ArrayList<Move> temp = new ArrayList<>();
            for (int i = 1; i < 8; i++) {
                Location tLoc = new Location(myR + combinations[j + 1] * i, myC + combinations[j] * i);
                if (!addMove(tLoc, piece, temp, board))
                    break;
            }
            ret.add(temp);
        }
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createBishopMoves(this, board);
    }
}

