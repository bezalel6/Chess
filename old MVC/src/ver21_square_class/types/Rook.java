package ver21_square_class.types;

import ver21_square_class.Location;
import ver21_square_class.model_classes.Board;
import ver21_square_class.moves.Move;

import java.util.ArrayList;

public class Rook extends Piece {
    public Rook(Location loc, int pieceColor) {
        super(loc, pieceColor, ROOK, "R");
    }

    public Rook(Piece other) {
        super(other);
    }

    public static ArrayList<ArrayList<Move>> createRookMoves(Piece piece, Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        Location myLoc = piece.getLoc();
        int myR = myLoc.getRow();
        int myC = myLoc.getCol();

        ArrayList<Move> temp = new ArrayList<Move>();
        for (int i = 1; i < ROWS; i++) {
            if (!addMove(myR + i, myC, piece, temp, board))
                break;
        }
        ArrayList<Move> finalTemp = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp);
        }});
        temp = new ArrayList<Move>();
        for (int i = 1; i < ROWS; i++) {
            if (!addMove(myR, myC + i, piece, temp, board))
                break;
        }

        ArrayList<Move> finalTemp1 = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp1);
        }});
        temp = new ArrayList<Move>();
        for (int i = 1; i < ROWS; i++) {
            if (!addMove(myR - i, myC, piece, temp, board))
                break;
        }
        ArrayList<Move> finalTemp2 = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp2);
        }});
        temp = new ArrayList<Move>();
        for (int i = 1; i < ROWS; i++) {
            if (!addMove(myR, myC - i, piece, temp, board))
                break;
        }
        ret.add(temp);

        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createRookMoves(this, board);
    }
}
