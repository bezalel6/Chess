package ver34_faster_move_generation.model_classes.pieces;

import ver34_faster_move_generation.Controller;
import ver34_faster_move_generation.Location;
import ver34_faster_move_generation.model_classes.Board;
import ver34_faster_move_generation.model_classes.moves.Move;

import java.util.ArrayList;

import static ver34_faster_move_generation.model_classes.moves.Castling.KING_SIDE;
import static ver34_faster_move_generation.model_classes.moves.Castling.QUEEN_SIDE;

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
                ret[row][col] = createRookMoves(loc, 0);
            }
        }
        return ret;
    }

    public static ArrayList<ArrayList<Move>> createRookMoves(Location movingFrom, int player) {
        return createRookMoves(movingFrom, player, ROOK);
    }

    public static ArrayList<ArrayList<Move>> createRookMoves(Location movingFrom, int player, int pieceType) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();

        ArrayList<Move> temp = new ArrayList<>();
        for (int i = 1; i < Controller.ROWS; i++) {
            Location loc = new Location(myR + i, myC);
            if (loc.isInBounds())
                temp.add(new Move(movingFrom, loc, player, pieceType));
        }
        ArrayList<Move> finalTemp = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp);
        }});
        temp = new ArrayList<>();
        for (int i = 1; i < Controller.ROWS; i++) {
            Location loc = new Location(myR, myC + i);
            if (loc.isInBounds())
                temp.add(new Move(movingFrom, loc, player, pieceType));
        }

        ArrayList<Move> finalTemp1 = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp1);
        }});
        temp = new ArrayList<>();
        for (int i = 1; i < Controller.ROWS; i++) {
            Location loc = new Location(myR - i, myC);
            if (loc.isInBounds())
                temp.add(new Move(movingFrom, loc, player, pieceType));
        }
        ArrayList<Move> finalTemp2 = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp2);
        }});
        temp = new ArrayList<>();
        for (int i = 1; i < Controller.ROWS; i++) {
            Location loc = new Location(myR, myC - i);
            if (loc.isInBounds())
                temp.add(new Move(movingFrom, loc, player, pieceType));
        }
        ret.add(temp);

        return ret;
    }

    public int getSideRelativeToKing(Board board) {
        int ret = QUEEN_SIDE;
        Piece king = board.getKing(getPieceColor());
        if (king.getStartingLoc().getCol() < getStartingLoc().getCol() * getDifference()) {
            ret = KING_SIDE;
        }
        return ret;
    }

    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves() {
//        return preCalculatedMoves[getLoc().getRow()][getLoc().getCol()];
        return createRookMoves(getLoc(), getPieceColor());
    }
}
