package ver4.SharedClasses.board_setup;

import ver4.SharedClasses.Location;
import ver4.SharedClasses.pieces.Piece;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

public class Board implements Iterable<Square>, Serializable {
    private final Square[] board;

    public Board() {
        board = new Square[Location.NUM_OF_SQUARES];
        for (int i = 0; i < board.length; i++) {
            board[i] = new Square(Location.getLoc(i));
        }
    }

    public Board(Board other) {
        this();
        for (Square square : other) {
            setPiece(square.getLoc(), square.getPiece());
        }
    }

    public void setPiece(Location loc, Piece piece) {
        getSquare(loc).setPiece(piece);
    }

    public Square getSquare(Location loc) {
        return board[loc.asInt()];
    }

    public Square[] getBoard() {
        return board;
    }

    public Square[] getRow(int row) {
        Square[] ret = new Square[8];
        IntStream.range(0, 8).forEach(col -> {
            Location loc = Location.getLoc(row, col, true);
            ret[col] = getSquare(loc);
        });
        return ret;
    }

    public Piece getPiece(Location loc) {
        return getSquare(loc).getPiece();
    }

    public Piece getPiece(Location loc, boolean notNull) {
        Piece ret = getPiece(loc);
        assert !notNull || ret != null;
        return ret;
    }

    @Override
    public Iterator<Square> iterator() {
        return Arrays.stream(board).iterator();
    }

    public void setSquareEmpty(Location loc) {
        getSquare(loc).setEmpty();
    }

    public boolean isSquareEmpty(Location loc) {
        return getSquare(loc).isEmpty();
    }

}
