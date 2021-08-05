package ver14_pieceLocation.types;

import ver14_pieceLocation.Board;
import ver14_pieceLocation.Location;
import ver14_pieceLocation.moves.Move;

import java.util.ArrayList;

public class Queen extends Piece {
    public static int worth = 9;

    public Queen(Location loc, Player pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, types.QUEEN, "Q", hasMoved);
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
