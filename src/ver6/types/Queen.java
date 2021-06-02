package ver6.types;

import ver6.Location;

import java.util.ArrayList;

public class Queen extends Piece {
    public Queen(Location loc, colors pieceColor) {
        super(9, loc, pieceColor, types.QUEEN, "Q");
    }

    @Override
    public ArrayList<Path> canMoveTo(Piece[][] pieces) {
        ArrayList<Path> ret = new ArrayList();
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
