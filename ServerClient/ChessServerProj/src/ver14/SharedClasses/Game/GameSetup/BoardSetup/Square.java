package ver14.SharedClasses.Game.GameSetup.BoardSetup;


import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.Location;

import java.io.Serializable;

/**
 * Square represents a square on the logic board.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Square implements Serializable {
    /**
     * The constant EMPTY_PIECE.
     */
    public static final Piece EMPTY_PIECE = null;
    private static final String EMPTY_PIECE_STR = "\u2003";
    //    private static final String EMPTY_PIECE_STR = "   ";
    private final Location loc;
    private Piece piece;

    /**
     * Instantiates a new Square.
     *
     * @param loc the loc
     */
    public Square(Location loc) {
        this.loc = loc;
        setEmpty();
    }

    /**
     * Sets empty.
     */
    public void setEmpty() {
        setPiece(EMPTY_PIECE);
    }

    /**
     * Instantiates a new Square.
     *
     * @param piece the piece
     * @param loc   the loc
     */
    public Square(Piece piece, Location loc) {
        this.piece = piece;
        this.loc = loc;
    }

    /**
     * Gets piece.
     *
     * @return the piece
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Sets piece.
     *
     * @param piece the piece
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Gets loc.
     *
     * @return the loc
     */
    public Location getLoc() {
        return loc;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return loc + ": " + getFen();
//        return getFen();
    }

    /**
     * Gets fen.
     *
     * @return the fen
     */
    public String getFen() {
        String ret = "";
        if (!isEmpty())
            ret = piece.getFen();
        return ret;
    }

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        return piece == EMPTY_PIECE;
    }

    /**
     * Gets piece icon.
     *
     * @return the piece icon
     */
    public String getPieceIcon() {
        if (isEmpty())
            return EMPTY_PIECE_STR;
        return piece.getPieceIcon();
    }
}
