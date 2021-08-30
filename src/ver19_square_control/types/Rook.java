package ver19_square_control.types;

import ver19_square_control.Board;
import ver19_square_control.Location;
import ver19_square_control.moves.Castling;
import ver19_square_control.moves.Move;

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

        ArrayList<Move> temp = new ArrayList<>();
        for (int i = 1; i < ROWS; i++) {
            if (!addMove(myR + i, myC, piece, temp, board))
                break;
        }
        ArrayList<Move> finalTemp = temp;
        ret.add(new ArrayList<>() {{
            addAll(finalTemp);
        }});
        temp = new ArrayList<>();
        for (int i = 1; i < ROWS; i++) {
            if (!addMove(myR, myC + i, piece, temp, board))
                break;
        }

        ArrayList<Move> finalTemp1 = temp;
        ret.add(new ArrayList<>() {{
            addAll(finalTemp1);
        }});
        temp = new ArrayList<>();
        for (int i = 1; i < ROWS; i++) {
            if (!addMove(myR - i, myC, piece, temp, board))
                break;
        }
        ArrayList<Move> finalTemp2 = temp;
        ret.add(new ArrayList<>() {{
            addAll(finalTemp2);
        }});
        temp = new ArrayList<>();
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
