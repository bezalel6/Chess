package ver14.SharedClasses.Game.GameSetup.BoardSetup;


import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.Location;

import java.io.Serializable;

/**
 * represents a square on the logic board.
 * with a {@link Location} and a {@link Piece}
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Square implements Serializable {
    /**
     * The constant EMPTY_PIECE.
     */
    public static final Piece EMPTY_PIECE = null;
    /**
     * The constant EMPTY_PIECE_STR.
     */
    private static final String EMPTY_PIECE_STR = "\u2003";
    /**
     * The constant loc.
     */
    public final Location loc;
    /**
     * The Piece.
     */
    private Piece piece;

    /**
     * Instantiates a new Square.
     *
     * @param loc the location of the square
     */
    public Square(Location loc) {
        this.loc = loc;
        setEmpty();
    }

    /**
     * Set this square to be empty.
     */
    public void setEmpty() {
        setPiece(EMPTY_PIECE);
    }


    /**
     * Gets the piece on this square.
     *
     * @return the piece if this square isn't empty, or {@link #EMPTY_PIECE} if it is.
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Sets a piece on this square.
     *
     * @param piece the piece
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
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
     * Gets the fen representation of this square.
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
     * Is this square empty.
     *
     * @return <code>true</code> is this square is empty
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
