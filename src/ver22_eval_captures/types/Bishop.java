package ver22_eval_captures.types;

import ver22_eval_captures.Location;
import ver22_eval_captures.model_classes.Board;
import ver22_eval_captures.moves.Move;

import java.util.ArrayList;

public class Bishop extends Piece {
    private static final int[] combinations = new int[]{
            1, 1,
            -1, -1,
            1, -1,
            -1, 1
    };
    public static double worth = 3.2;

    public Bishop(Location loc, int pieceColor) {
        super(loc, pieceColor, BISHOP, "B");
    }

    public Bishop(Piece other) {
        super(other);
    }

    public static ArrayList<ArrayList<Move>> createBishopMoves(Location movingFrom, int player, Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();

        for (int j = 0; j < 4; j++) {
            ArrayList<Move> temp = new ArrayList<>();
            for (int i = 1; i < 8; i++) {
                Location tLoc = new Location(myR + combinations[j + 1] * i, myC + combinations[j] * i);
                if (!addMove(movingFrom, tLoc, player, temp, board))
                    break;
            }
            ret.add(temp);
        }
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createBishopMoves(getLoc(), getPieceColor(), board);
    }
}

