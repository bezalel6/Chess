package ver24_flip_board.model_classes.pieces;

import ver24_flip_board.Controller;
import ver24_flip_board.Location;
import ver24_flip_board.model_classes.Board;
import ver24_flip_board.moves.Move;

import java.util.ArrayList;

import static ver24_flip_board.moves.Castling.KING_SIDE;
import static ver24_flip_board.moves.Castling.QUEEN_SIDE;

public class Rook extends Piece {
    public Rook(Location loc, int pieceColor) {
        super(loc, pieceColor, ROOK, "R");
    }

    public Rook(Piece other) {
        this(other.getStartingLoc(), other.getPieceColor());
        setLoc(other.getLoc());
    }

    public static ArrayList<ArrayList<Move>> createRookMoves(Location movingFrom, int player, Board board) {
        return createRookMoves(movingFrom, player, true, board);
    }

    public static ArrayList<ArrayList<Move>> createRookMoves(Location movingFrom, int player, boolean initializeMoves, Board board) {
        return createRookMoves(movingFrom, player, ROOK, initializeMoves, board);
    }

    public static ArrayList<ArrayList<Move>> createRookMoves(Location movingFrom, int player, int pieceType, boolean initializeMoves, Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        int myR = movingFrom.getRow();
        int myC = movingFrom.getCol();

        ArrayList<Move> temp = new ArrayList<Move>();
        for (int i = 1; i < Controller.ROWS; i++) {
            if (!addMove(new Move(movingFrom, new Location(myR + i, myC), player, ROOK, initializeMoves), player, temp, board))
                break;
        }
        ArrayList<Move> finalTemp = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp);
        }});
        temp = new ArrayList<Move>();
        for (int i = 1; i < Controller.ROWS; i++) {
            if (!addMove(new Move(movingFrom, new Location(myR, myC + i), player, ROOK, initializeMoves), player, temp, board))
                break;
        }

        ArrayList<Move> finalTemp1 = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp1);
        }});
        temp = new ArrayList<Move>();
        for (int i = 1; i < Controller.ROWS; i++) {
            if (!addMove(new Move(movingFrom, new Location(myR - i, myC), player, ROOK, initializeMoves), player, temp, board))
                break;
        }
        ArrayList<Move> finalTemp2 = temp;
        ret.add(new ArrayList<Move>() {{
            addAll(finalTemp2);
        }});
        temp = new ArrayList<Move>();
        for (int i = 1; i < Controller.ROWS; i++) {
            if (!addMove(new Move(movingFrom, new Location(myR, myC - i), player, ROOK, initializeMoves), player, temp, board))
                break;
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
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        return createRookMoves(getLoc(), getPieceColor(), board);
    }
}
