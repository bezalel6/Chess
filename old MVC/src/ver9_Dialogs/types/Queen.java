package ver9_Dialogs.types;

import ver9_Dialogs.Location;
import ver9_Dialogs.Move;

import java.util.ArrayList;

public class Queen extends Piece {
    public static int worth = 9;

    public Queen(Location loc, colors pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, types.QUEEN, "Q", hasMoved);
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
