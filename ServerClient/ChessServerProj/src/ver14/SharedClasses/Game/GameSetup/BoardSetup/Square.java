package ver14.SharedClasses.Game.GameSetup.BoardSetup;


import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.Location;

import java.io.Serializable;

/*
 * Square
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * Square -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * Square -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class Square implements Serializable {
    public static final Piece EMPTY_PIECE = null;
    private static final String EMPTY_PIECE_STR = "\u2003";
    //    private static final String EMPTY_PIECE_STR = "   ";
    private final Location loc;
    private Piece piece;

    public Square(Location loc) {
        this.loc = loc;
        setEmpty();
    }

    public void setEmpty() {
        setPiece(EMPTY_PIECE);
    }

    public Square(Piece piece, Location loc) {
        this.piece = piece;
        this.loc = loc;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Location getLoc() {
        return loc;
    }

    @Override
    public String toString() {
        return loc + ": " + getFen();
//        return getFen();
    }

    public String getFen() {
        String ret = "";
        if (!isEmpty())
            ret = piece.getFen();
        return ret;
    }

    public boolean isEmpty() {
        return piece == EMPTY_PIECE;
    }

    public String getPieceIcon() {
        if (isEmpty())
            return EMPTY_PIECE_STR;
        return piece.getPieceIcon();
    }
}
