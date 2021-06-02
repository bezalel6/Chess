package ver7_without_path.types;

import ver7_without_path.Location;
import ver7_without_path.Move;

import java.util.ArrayList;

public class Queen extends Piece {
    public Queen(Location loc, colors pieceColor) {
        super(9, loc, pieceColor, types.QUEEN, "Q");
    }

    @Override
    public ArrayList<Move> canMoveTo(Piece[][] pieces) {
        ArrayList<Move> ret = new ArrayList();
        Location loc = getLoc();
        Rook rook = new Rook(loc, getPieceColor());
        addAll(ret, rook.canMoveTo(pieces), pieces);
        Bishop bishop = new Bishop(loc, getPieceColor());
        addAll(ret, bishop.canMoveTo(pieces), pieces);
        King king = new King(loc, getPieceColor());
        addAll(ret, king.canMoveTo(pieces), pieces);

        return ret;
    }
}
