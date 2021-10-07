package ver22_eval_captures.types;

import ver22_eval_captures.Location;
import ver22_eval_captures.model_classes.Board;
import ver22_eval_captures.moves.Move;

import java.util.ArrayList;

public class Rook extends Piece {
    public Rook(Location loc, int pieceColor) {
        super(loc, pieceColor, ROOK, "R");
    }

    public Rook(Piece other) {
        this(other.getStartingLoc(), other.getPieceColor());
        setLoc(other.getLoc());
    }

    public static ArrayList<ArrayList<Move>> createRookMoves(Location movingFrom, int player, Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();

        ArrayList<Move> temp = new ArrayList<Move>();
        for (int i = 1; i < ROWS; i++) {
            if (!addMove(new Move(movingFrom, myR + i, myC, board), player, temp, board))
                break;
        }
        ArrayList<Move> finalTemp = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp);
        }});
        temp = new ArrayList<Move>();
        for (int i = 1; i < ROWS; i++) {
            if (!addMove(new Move(movingFrom, myR, myC + i, board), player, temp, board))
                break;
        }

        ArrayList<Move> finalTemp1 = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp1);
        }});
        temp = new ArrayList<Move>();
        for (int i = 1; i < ROWS; i++) {
            if (!addMove(new Move(movingFrom, myR - i, myC, board), player, temp, board))
                break;
        }
        ArrayList<Move> finalTemp2 = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp2);
        }});
        temp = new ArrayList<Move>();
        for (int i = 1; i < ROWS; i++) {
            if (!addMove(new Move(movingFrom, myR, myC - i, board), player, temp, board))
                break;
        }
        ret.add(temp);

        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createRookMoves(getLoc(), getPieceColor(), board);
    }
}
