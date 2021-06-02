package ver12_myJbutton.types;

import ver12_myJbutton.Board;
import ver12_myJbutton.Location;
import ver12_myJbutton.moves.Castling;
import ver12_myJbutton.moves.Move;

import java.util.ArrayList;

public class King extends Piece {
    public static int worth = 3;

    public King(Location loc, Player pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, types.KING, "K", hasMoved);
    }

    public King(Piece other) {
        super(other);
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
                        if (isInBounds(kingMiddleMove) && isInBounds(kingFinalLoc) && isInBounds(rookFinalLoc) && isInBounds(rookMiddleLoc))
                            //if (!board.isInCheck(getPieceColor()) && !board.isSquareThreatened(kingMiddleMove, getOtherColor()) && !board.isSquareThreatened(kingFinalLoc, getOtherColor()))
                            add(ret, new Castling(new Move(pieceLoc, kingFinalLoc, false, board), (Rook) p, kingMiddleMove, kingFinalLoc, rookFinalLoc, rookMiddleLoc, board), board);
                    } else {
                        Location kingMiddleMove = new Location(myR, myC + 1), kingFinalLoc = new Location(myR, myC + 2), rookFinalLoc = new Location(myR, myC + 1);
                        if (isInBounds(kingMiddleMove) && isInBounds(kingFinalLoc) && isInBounds(rookFinalLoc))
                            add(ret, new Castling(new Move(pieceLoc, kingFinalLoc, false, board), (Rook) p, kingMiddleMove, kingFinalLoc, rookFinalLoc, board), board);
                    }


                }
            }
        }
        return ret;
    }

}
