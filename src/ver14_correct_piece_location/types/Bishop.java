package ver14_correct_piece_location.types;

import ver14_correct_piece_location.Board;
import ver14_correct_piece_location.Location;
import ver14_correct_piece_location.moves.Move;

import java.util.ArrayList;

public class Bishop extends Piece {
    public static double worth = 3.2;

    public Bishop(Location loc, Player pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, types.BISHOP, "B", hasMoved);
    }

    public Bishop(Piece other) {
        super(other);
    }

    @Override
    public ArrayList<Move> canMoveTo(Board board) {
        ArrayList<Move> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR + i, myC + i);
            if (!isInBounds(loc)) break;
            Piece piece = board.getPiece(loc);
            if (piece != null) {
                if (!isOnMyTeam(piece))
                    add(ret, loc, board);
                break;
            }
            if (loc.getCol() > COLS - 1 || loc.getRow() > COLS - 1)
                break;

            add(ret, loc, board);
        }
        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR - i, myC - i);
            if (!isInBounds(loc)) break;
            Piece m = board.getPiece(loc);
            if (m != null) {
                if (!isOnMyTeam(m))
                    add(ret, loc, board);
                break;
            }
            if (loc.getCol() < 0 || loc.getRow() < 0)
                break;
            add(ret, loc, board);
        }

        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR + i, myC - i);
            if (!isInBounds(loc)) break;
            Piece piece = board.getPiece(loc);
            if (piece != null) {
                if (!isOnMyTeam(piece))
                    add(ret, loc, board);
                break;
            }
            if (loc.getCol() < 0 || loc.getRow() > COLS - 1)
                break;
            add(ret, loc, board);
        }
        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR - i, myC + i);
            if (!isInBounds(loc)) break;
            Piece m = board.getPiece(loc);
            if (m != null) {
                if (!isOnMyTeam(m))
                    add(ret, loc, board);
                break;
            }
            if (loc.getCol() > COLS - 1 || loc.getRow() < 0)
                break;
            add(ret, loc, board);
        }
        return ret;

    }
}
