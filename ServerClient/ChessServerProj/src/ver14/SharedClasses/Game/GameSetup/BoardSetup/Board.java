package ver14.SharedClasses.Game.GameSetup.BoardSetup;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.Location;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Board - represents the logic board of the model
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Board implements Iterable<Square>, Serializable {

    /**
     * The constant startingFen.
     */
//    todo turn into a hashmap
    public final static String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    /**
     * The constant example.
     */
    public static final Board example = new Board() {{
        fenSetup("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }};
//    private final Square[] board;

    private final Map<Location, Square> boardMap;

    /**
     * Instantiates a new Board.
     *
     * @param other the other
     */
    public Board(Board other) {
        this();
        for (Square square : other) {
            setPiece(square.getLoc(), square.getPiece());
        }
    }

    /**
     * Instantiates a new Board.
     */
    public Board() {
        this.boardMap = new HashMap<>();
        for (Location loc : Location.ALL_LOCS) {
            boardMap.put(loc, new Square(loc));
        }
    }

    /**
     * Sets piece.
     *
     * @param loc   the loc
     * @param piece the piece
     */
    public void setPiece(Location loc, Piece piece) {
//        if (Location.isInBounds(loc))
        getSquare(loc).setPiece(piece);
    }

    /**
     * Gets square.
     *
     * @param loc the loc
     * @return the square
     */
    public Square getSquare(Location loc) {
        return boardMap.get(loc);
    }

    /**
     * Instantiates a new Board.
     *
     * @param fen the fen
     */
    public Board(String fen) {
        this();
        fenSetup(fen);
    }

    /**
     * Fen setup.
     *
     * @param fen the fen
     */
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
//                    if (Location.flip_fen_load_locs) {
//                    currentLoc = currentLoc.flip();
//                    }
                    Piece piece = Piece.getPieceFromFen(currentChar);
                    setPiece(currentLoc, piece);

                    col++;
                } else {
                    col += Integer.parseInt(currentChar + "");
                }
            }
        }
    }

    /**
     * Starting pos board.
     *
     * @return the board
     */
    public static Board startingPos() {
        return new Board() {{
            fenSetup(startingFen);
        }};
    }

    /**
     * Get row square [ ].
     *
     * @param row      the row
     * @param flipLocs the flip locs
     * @return the square [ ]
     */
    public Square[] getRow(int row, boolean flipLocs) {
        Square[] ret = new Square[8];
        IntStream.range(0, 8).forEach(col -> {
//            col = Location.flip(col);
            Location loc = Location.getLoc(row, col, flipLocs);
            ret[col] = getSquare(loc);
        });
        return ret;
    }

//    public Square[] getBoard() {
//        return board;
//    }

    /**
     * Gets piece.
     *
     * @param loc     the loc
     * @param notNull the not null
     * @return the piece
     */
    public Piece getPiece(Location loc, boolean notNull) {
        Piece ret = getPiece(loc);
        assert !notNull || ret != null;
        return ret;
    }

    /**
     * Gets piece.
     *
     * @param loc the loc
     * @return the piece
     */
    public Piece getPiece(Location loc) {
        return getSquare(loc).getPiece();
    }

    /**
     * Print.
     */
    public void print() {
        System.out.println(this);
    }

    /**
     * Iterator iterator.
     *
     * @return the iterator
     */
    @Override
    public Iterator<Square> iterator() {
        return boardMap.values().iterator();
    }

    /**
     * Sets square empty.
     *
     * @param loc the loc
     */
    public void setSquareEmpty(Location loc) {
        getSquare(loc).setEmpty();
    }

    /**
     * Is square empty boolean.
     *
     * @param loc the loc
     * @return the boolean
     */
    public boolean isSquareEmpty(Location loc) {
        return getSquare(loc).isEmpty();
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        String divider = "|";
        StringBuilder ret = new StringBuilder();

        ret.append(" ");
        for (int j = 0; j < 8; j++) {
            char a = 'a';
            a += j;
            ret.append(" ").append(a).append(" ");
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
