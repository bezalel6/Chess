package ver2.types;

import ver2.Location;

import java.util.ArrayList;

public class Queen extends Piece {
    public Queen(  Location loc, colors pieceColor) {
        super(9, loc, pieceColor, types.Queen, "Q");
    }

    @Override
    public ArrayList<Path> canMoveTo() {
        ArrayList<Path> ret = new ArrayList();
        Location loc = getLoc();
        Rook rook = new Rook(loc, pieceColor);
        addAll(ret,rook.canMoveTo());
        Bishop bishop = new Bishop(loc, pieceColor);
        addAll(ret,bishop.canMoveTo());
        King king = new King(loc, pieceColor);
        addAll(ret,king.canMoveTo());
        return ret;
    }
}
