package ver15_new_piece_tables.types;

import ver15_new_piece_tables.Board;
import ver15_new_piece_tables.Location;
import ver15_new_piece_tables.moves.Move;

import java.util.ArrayList;

public class Queen extends Piece {
    public static int worth = 9;

    public Queen(Location loc, Player pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, PieceTypes.QUEEN, "Q", hasMoved);
    }

    public Queen(Piece other) {
        super(other);
    }

    @Override
    public ArrayList<Move> canMoveTo(Board board) {
        ArrayList<Move> ret = new ArrayList();
        Location loc = getLoc();
        Rook rook = new Rook(loc, getPieceColor(), true);
        addAll(ret, rook.canMoveTo(board), board);
        Bishop bishop = new Bishop(loc, getPieceColor(), true);
        addAll(ret, bishop.canMoveTo(board), board);
        return ret;
    }
}
