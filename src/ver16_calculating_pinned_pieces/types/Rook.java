package ver16_calculating_pinned_pieces.types;

import ver16_calculating_pinned_pieces.Board;
import ver16_calculating_pinned_pieces.Location;
import ver16_calculating_pinned_pieces.moves.Move;

import java.util.ArrayList;

public class Rook extends Piece {
    public static int worth = 5;

    public Rook(Location loc, int pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, PieceTypes.ROOK, "R", hasMoved);
    }

    public Rook(Piece other) {
        super(other);
    }

    @Override
    public ArrayList<Move> canMoveTo(Board board) {
        ArrayList<Move> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        Location foundFirstPiece = null;
        Piece hitPiece = null;
        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR, myC + i);
            if (hitPiece != null) break;
            else if (foundFirstPiece != null) {

                hitPiece = board.getPiece(loc);
                Piece skeweredPiece = board.getPiece(foundFirstPiece);
                xRay(skeweredPiece, hitPiece);
            }
            Move res = add(ret, loc, board);
            if (res == null) break;
            if (res.getBoard() == null) foundFirstPiece = loc;
        }
        foundFirstPiece = null;
        hitPiece = null;
        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR, myC - i);

            if (hitPiece != null) break;
            else if (foundFirstPiece != null) {
                hitPiece = board.getPiece(loc);
                Piece skeweredPiece = board.getPiece(foundFirstPiece);
                xRay(skeweredPiece, hitPiece);
            }
            Move res = add(ret, loc, board);
            if (res == null) break;
            if (res.getBoard() == null) foundFirstPiece = loc;
        }

        foundFirstPiece = null;
        hitPiece = null;
        for (int i = 1; i < ROWS; i++) {
            Location loc = new Location(myR + i, myC);
            if (hitPiece != null) break;
            else if (foundFirstPiece != null) {
                hitPiece = board.getPiece(loc);
                Piece skeweredPiece = board.getPiece(foundFirstPiece);
                xRay(skeweredPiece, hitPiece);
            }
            Move res = add(ret, loc, board);
            if (res == null) break;
            if (res.getBoard() == null) foundFirstPiece = loc;
        }
        foundFirstPiece = null;
        hitPiece = null;

        for (int i = 1; i < ROWS; i++) {
            Location loc = new Location(myR - i, myC);
            if (hitPiece != null) break;
            else if (foundFirstPiece != null) {
                hitPiece = board.getPiece(loc);
                Piece skeweredPiece = board.getPiece(foundFirstPiece);
                xRay(skeweredPiece, hitPiece);
            }
            Move res = add(ret, loc, board);
            if (res == null) break;
            if (res.getBoard() == null) foundFirstPiece = loc;
        }
        return ret;
    }
}
