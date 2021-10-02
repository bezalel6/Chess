package ver23_minimax_levels.model_classes;


import ver23_minimax_levels.MyError;
import ver23_minimax_levels.Location;
import ver23_minimax_levels.types.Piece;

import java.util.ArrayList;
import java.util.HashMap;

public class Square {
    public static final Piece EMPTY_PIECE = null;
    private static final String EMPTY_PIECE_STR = "\u2003";
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
        this.piece = EMPTY_PIECE;
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
        return piece.getPieceFen();
    }

    @Override
    public String toString() {
        String ret = EMPTY_PIECE_STR;
        if (!isEmpty())
            ret = piece.getPieceIcon();
        return ret;
    }
}
