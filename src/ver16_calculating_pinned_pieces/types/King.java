package ver16_calculating_pinned_pieces.types;

import ver16_calculating_pinned_pieces.Board;
import ver16_calculating_pinned_pieces.Location;
import ver16_calculating_pinned_pieces.moves.Castling;
import ver16_calculating_pinned_pieces.moves.Move;

import java.util.ArrayList;

public class King extends Piece {
    public static int worth = 200;

    public King(Location loc, int pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, PieceTypes.KING, "K", hasMoved);
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

                    if (myC > rookLoc.getCol() && myC - 1 > -1 && myC - 2 > -1) {//O-O-O
                        Location kingMiddleMove = new Location(myR, myC - 1), kingFinalLoc = new Location(myR, myC - 2), rookFinalLoc = new Location(myR, myC - 1), rookMiddleLoc = new Location(myR, myC - 3);
                        if (isInBounds(kingMiddleMove) && isInBounds(kingFinalLoc) && isInBounds(rookFinalLoc) && isInBounds(rookMiddleLoc)
                                && board.isSquareEmpty(kingMiddleMove) && board.isSquareEmpty(kingFinalLoc) && board.isSquareEmpty(rookMiddleLoc))
                            add(ret, new Castling(new Move(pieceLoc, kingFinalLoc, false, board), (Rook) p, kingMiddleMove, kingFinalLoc, rookFinalLoc, rookMiddleLoc), board);
                    } else {//O-O
                        Location kingMiddleMove = new Location(myR, myC + 1), kingFinalLoc = new Location(myR, myC + 2), rookFinalLoc = new Location(myR, myC + 1);
                        if (isInBounds(kingMiddleMove) && isInBounds(kingFinalLoc) && isInBounds(rookFinalLoc)
                                && board.isSquareEmpty(kingMiddleMove) && board.isSquareEmpty(kingFinalLoc))
                            add(ret, new Castling(new Move(pieceLoc, kingFinalLoc, false, board), (Rook) p, kingMiddleMove, kingFinalLoc, rookFinalLoc), board);
                    }


                }
            }
        }
        return ret;
    }

}
