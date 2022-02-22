package ver5.SharedClasses.board_setup;


import ver5.SharedClasses.Location;
import ver5.SharedClasses.pieces.Piece;

import java.io.Serializable;

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

    public Square(Piece piece, Location loc) {
        this.piece = piece;
        this.loc = loc;
    }

    public void setEmpty() {
        setPiece(EMPTY_PIECE);
    }

    public boolean isEmpty() {
        return piece == EMPTY_PIECE;
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

    public String getFen() {
        String ret = "";
        if (!isEmpty())
            ret = piece.getFen();
        return ret;
    }

    @Override
    public String toString() {
        return getFen();
    }

    public String getPieceIcon() {
        if (isEmpty())
            return EMPTY_PIECE_STR;
        return piece.getPieceIcon();
    }
}
