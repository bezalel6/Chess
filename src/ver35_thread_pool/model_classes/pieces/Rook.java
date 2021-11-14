package ver35_thread_pool.model_classes.pieces;

import ver35_thread_pool.Controller;
import ver35_thread_pool.Location;
import ver35_thread_pool.model_classes.Board;
import ver35_thread_pool.model_classes.CastlingAbility;
import ver35_thread_pool.model_classes.moves.Move;

import java.util.ArrayList;

public class Rook extends Piece {
    private static final ArrayList<ArrayList<Move>>[][] preCalculatedMoves = calc();

    public Rook(Location loc, int pieceColor) {
        super(loc, pieceColor, ROOK, "R");
    }

    public Rook(Piece other) {
        this(other.getStartingLoc(), other.getPieceColor());
        setLoc(other.getLoc());
    }

    private static ArrayList<ArrayList<Move>>[][] calc() {
        ArrayList<ArrayList<Move>>[][] ret = new ArrayList[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Location loc = new Location(row, col);
                ret[row][col] = generateMoves(loc);
            }
        }
        return ret;
    }

    private static ArrayList<ArrayList<Move>> generateMoves(Location movingFrom) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();

        ArrayList<Move> temp = new ArrayList<>();
        for (int i = 1; i < Controller.ROWS; i++) {
            Location loc = new Location(myR + i, myC);
            if (loc.isInBounds())
                temp.add(new Move(movingFrom, loc));
        }
        ArrayList<Move> finalTemp = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp);
        }});
        temp = new ArrayList<>();
        for (int i = 1; i < Controller.ROWS; i++) {
            Location loc = new Location(myR, myC + i);
            if (loc.isInBounds())
                temp.add(new Move(movingFrom, loc));
        }

        ArrayList<Move> finalTemp1 = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp1);
        }});
        temp = new ArrayList<>();
        for (int i = 1; i < Controller.ROWS; i++) {
            Location loc = new Location(myR - i, myC);
            if (loc.isInBounds())
                temp.add(new Move(movingFrom, loc));
        }
        ArrayList<Move> finalTemp2 = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp2);
        }});
        temp = new ArrayList<>();
        for (int i = 1; i < Controller.ROWS; i++) {
            Location loc = new Location(myR, myC - i);
            if (loc.isInBounds())
                temp.add(new Move(movingFrom, loc));
        }
        ret.add(temp);

        return ret;
    }

    public static ArrayList<ArrayList<Move>> getPseudoRookMoves(Location movingFrom) {
        return preCalculatedMoves[movingFrom.getRow()][movingFrom.getCol()];
    }

    public int getSideRelativeToKing(Board board) {
        int ret = CastlingAbility.QUEEN_SIDE;
        Piece king = board.getKing(getPieceColor());
        if (king.getStartingLoc().getCol() < getStartingLoc().getCol() * getDifference()) {
            ret = CastlingAbility.KING_SIDE;
        }
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves() {
        return getPseudoRookMoves(getLoc());
    }


}
