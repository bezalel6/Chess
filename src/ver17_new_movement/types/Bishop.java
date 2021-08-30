package ver17_new_movement.types;

import ver17_new_movement.Board;
import ver17_new_movement.Location;
import ver17_new_movement.moves.Move;

import java.util.ArrayList;

public class Bishop extends Piece {
    public static double worth = 3.2;
    private static int[] combinations = new int[]{
            1, 1,
            -1, -1,
            1, -1,
            -1, 1
    };

    public Bishop(Location loc, int pieceColor, boolean hasMoved) {
        super(loc, pieceColor, BISHOP, "B", hasMoved);
    }

    public Bishop(Piece other) {
        super(other);
    }


    private static void addCombo(int a, int b, int myR, int myC, ArrayList<Move> temp, Location pieceLoc, Board board) {
        temp.add(new Move(pieceLoc, new Location(myR + a, myC + b), board));
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();

        for (int j = 0; j < 4; j++) {
            ArrayList<Move> temp = new ArrayList<>();
            for (int i = 1; i < 8; i++) {
                Location loc = new Location(myR + combinations[j + 1] * i, myC + combinations[j] * i);
                if (isInBounds(loc)) {
                    temp.add(new Move(pieceLoc, loc, board));
                }
            }
            ret.add(temp);
        }
        return ret;
    }
}

