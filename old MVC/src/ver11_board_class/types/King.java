package ver11_board_class.types;

import ver11_board_class.Board;
import ver11_board_class.Location;
import ver11_board_class.moves.Castling;
import ver11_board_class.moves.Move;

import java.util.ArrayList;

public class King extends Piece {
    public static int worth = 3;

    public King(Location loc, Player pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, types.KING, "K", hasMoved);
    }

    @Override
    public ArrayList<Move> canMoveTo(Board board) {
        ArrayList<Move> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        add(ret, myR + 1, myC, board);
        add(ret, myR, myC + 1, board);
        add(ret, myR + 1, myC + 1, board);
        add(ret, myR - 1, myC, board);
        add(ret, myR, myC - 1, board);
        add(ret, myR - 1, myC - 1, board);
        add(ret, myR + 1, myC - 1, board);
        add(ret, myR - 1, myC + 1, board);
        for (Piece[] row : board) {
            for (Piece p : row) {
                if (!getHasMoved() && p != null && p.isOnMyTeam(this) && p instanceof Rook && !p.getHasMoved()) {
                    Location rookLoc = p.getLoc();

                    if (myC > rookLoc.getCol() && myC - 1 > -1 && myC - 2 > -1) {
                        Location kingMiddleMove = new Location(myR, myC - 1), kingFinalLoc = new Location(myR, myC - 2), rookFinalLoc = new Location(myR, myC - 1), rookMiddleLoc = new Location(myR, myC - 3);
                        add(ret, new Castling(new Move(pieceLoc, kingFinalLoc, false, board), (Rook) p, kingFinalLoc, rookFinalLoc, rookMiddleLoc, board), board);
                    } else {
                        Location kingMiddleMove = new Location(myR, myC + 1), kingFinalLoc = new Location(myR, myC + 2), rookFinalLoc = new Location(myR, myC + 1);

                        add(ret, new Castling(new Move(pieceLoc, kingFinalLoc, false, board), (Rook) p, kingMiddleMove, kingFinalLoc, rookFinalLoc, board), board);

                    }

                }
            }
        }
        return ret;
    }

}
