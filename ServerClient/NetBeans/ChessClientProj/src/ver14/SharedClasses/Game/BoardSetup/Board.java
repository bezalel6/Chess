package ver14.SharedClasses.Game.BoardSetup;

import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.pieces.Piece;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

public class Board implements Iterable<Square>, Serializable {

    //    todo turn into a hashmap
    public final static String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static final Board example = new Board() {{
        fenSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }};
    private final Square[] board;

    public Board(Board other) {
        this();
        for (Square square : other) {
            setPiece(square.getLoc(), square.getPiece());
        }
    }

    public Board() {
        board = new Square[Location.NUM_OF_SQUARES];
        for (int i = 0; i < board.length; i++) {
            board[i] = new Square(Location.getLoc(i));
        }
    }

    public void setPiece(Location loc, Piece piece) {
//        if (Location.isInBounds(loc))
        getSquare(loc).setPiece(piece);
    }

    public Square getSquare(Location loc) {
        return board[loc.asInt];
    }

    public Board(String fen) {
        this();
        fenSetup(fen);
    }

    public void fenSetup(String fen) {
        if (fen == null || fen.trim().equals("")) {
            fen = startingFen;
        }
        String[] rows = fen.split(" ")[0].split("/");

        /*
         * when loading a fen we might need to flip the locations bc a fen is structured with the white pieces on the bottom. and thats not how we want our mat to work.
         * */

        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            String row = rows[rowIndex];
            int col = 0;
            for (char currentChar : row.toCharArray()) {
                if (Character.isLetter(currentChar)) {
                    Location currentLoc = Location.getLoc(rowIndex, col);
                    if (Location.flip_fen_load_locs) {
                        currentLoc = currentLoc.flip();
                    }
                    Piece piece = Piece.getPieceFromFen(currentChar);
                    setPiece(currentLoc, piece);

                    col++;
                } else {
                    col += Integer.parseInt(currentChar + "");
                }
            }
        }
    }

    public static Board startingPos() {
        return new Board() {{
            fenSetup(startingFen);
        }};
    }

    public Square[] getRow(int row, boolean flipLocs) {
        Square[] ret = new Square[8];
        IntStream.range(0, 8).forEach(col -> {
//            col = Location.flip(col);
            Location loc = Location.getLoc(row, col, flipLocs);
            ret[col] = getSquare(loc);
        });
        return ret;
    }

    public Square[] getBoard() {
        return board;
    }

    public Piece getPiece(Location loc, boolean notNull) {
        Piece ret = getPiece(loc);
        assert !notNull || ret != null;
        return ret;
    }

    public Piece getPiece(Location loc) {
        return getSquare(loc).getPiece();
    }

    public void print() {
        System.out.println(this);
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

    @Override
    public String toString() {
        String divider = "|";
        StringBuilder ret = new StringBuilder();

        ret.append(" ");
        for (int j = 0; j < 8; j++) {
            ret.append(" ").append((char) ('ａ' + j)).append(" ");
        }
        ret.append("\n");

        for (int r = 0, rowNum = 8 - 1; r < 8; r++, rowNum--) {
            ret.append(rowNum + 1);
            for (int c = 0; c < 8; c++) {
                Location loc = Location.getLoc(r, c);
                ret.append(divider).append(getSquare(loc).getPieceIcon()).append(divider);
            }
            ret.append("\n");
        }

        return ret.toString();
    }

}