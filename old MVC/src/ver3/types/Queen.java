package ver3.types;

import ver3.Location;

import java.util.ArrayList;

public class Queen extends Piece {
    public Queen(Location loc, int pieceColor) {
        super(9, loc, pieceColor, QUEEN, "Q");
    }

    @Override
    public ArrayList<ver3.types.Path> canMoveTo(Piece[][] pieces, boolean isCheckKing) {
        ArrayList<Path> ret = new ArrayList();
        Location loc = getLoc();
        ver3.types.Rook rook = new Rook(loc, getPieceColor());
        addAll(ret, rook.canMoveTo(pieces, isCheckKing), pieces);
        Bishop bishop = new Bishop(loc, getPieceColor());
        addAll(ret, bishop.canMoveTo(pieces, isCheckKing), pieces);
        ver3.types.King king = new King(loc, getPieceColor());
        addAll(ret, king.canMoveTo(pieces, isCheckKing), pieces);

        return ret;
    }
}
