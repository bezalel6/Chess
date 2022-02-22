package ver15_new_piece_tables.types;

import ver15_new_piece_tables.Board;
import ver15_new_piece_tables.Location;
import ver15_new_piece_tables.moves.Move;

import java.util.ArrayList;

public class Rook extends Piece {
    public static int worth = 5;

    public Rook(Location loc, Player pieceColor, boolean hasMoved) {
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

        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR, myC + i);
            if (!isInBounds(loc)) break;
            Piece piece = board.getPiece(loc);
            if (piece != null) {
                if (!isOnMyTeam(piece))
                    add(ret, loc, board);
                break;
            }
            if (loc.getCol() > COLS - 1)
                break;
            add(ret, loc, board);
        }
        for (int i = 1; i < COLS; i++) {
            Location loc = new Location(myR, myC - i);
            if (!isInBounds(loc)) break;
            Piece piece = board.getPiece(loc);
            if (piece != null) {
                if (!isOnMyTeam(piece))
                    add(ret, loc, board);
                break;
            }
            if (loc.getCol() < 0)
                break;
            add(ret, loc, board);
        }

        for (int i = 1; i < ROWS; i++) {
            Location loc = new Location(myR + i, myC);
            if (!isInBounds(loc)) break;
            Piece piece = board.getPiece(loc);
            if (piece != null) {
                if (!isOnMyTeam(piece))
                    add(ret, loc, board);
                break;
            }
            if (loc.getCol() > ROWS - 1)
                break;
            add(ret, loc, board);
        }
        for (int i = 1; i < ROWS; i++) {
            Location loc = new Location(myR - i, myC);
            if (!isInBounds(loc)) break;
            Piece piece = board.getPiece(loc);
            if (piece != null) {
                if (!isOnMyTeam(piece))
                    add(ret, loc, board);
                break;
            }
            if (loc.getRow() < 0)
                break;
            add(ret, loc, board);
        }
        return ret;
    }
}
