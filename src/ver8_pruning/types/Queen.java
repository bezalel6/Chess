package ver8_pruning.types;

import ver8_pruning.Location;
import ver8_pruning.Move;

import java.util.ArrayList;

public class Queen extends Piece {
    public Queen(Location loc, colors pieceColor, boolean hasMoved) {
        super(9, loc, pieceColor, types.QUEEN, "Q",hasMoved);
    }

    @Override
    public ArrayList<Move> canMoveTo(Piece[][] pieces) {
        ArrayList<Move> ret = new ArrayList();
        Location loc = getLoc();
        Rook rook = new Rook(loc, getPieceColor(), true);
        addAll(ret, rook.canMoveTo(pieces), pieces);
        Bishop bishop = new Bishop(loc, getPieceColor(), true);
        addAll(ret, bishop.canMoveTo(pieces), pieces);
        return ret;
    }
}
