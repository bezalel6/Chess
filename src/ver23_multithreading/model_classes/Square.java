package ver23_multithreading.model_classes;


import ver23_multithreading.Error;
import ver23_multithreading.Location;
import ver23_multithreading.types.Piece;

import java.util.ArrayList;

public class Square {
    public static final Piece EMPTY_PIECE = null;
    private static String EMPTY_PIECE_STR = "\u2003";

    private Piece piece;
    private ArrayList<Piece> capturedHereList;
    private Location loc;

    public Square(Location loc) {
        this.loc = loc;
        setEmpty();
        initCapturedHereList();
    }

    public Square(Piece piece, Location loc) {
        this.piece = piece;
        this.loc = loc;
        initCapturedHereList();
    }

    private void initCapturedHereList() {
        capturedHereList = new ArrayList<>();
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

    public ArrayList<Piece> getCapturedHereList() {
        return capturedHereList;
    }

    public Piece getCapturedPiece(int pieceHash) {
        for (Piece piece : capturedHereList) {
            if (piece.hashCode() == pieceHash)
                return piece;
        }
        Error.error("didnt find captured piece");
        return null;
    }

    public void revivePiece(int pieceHash) {
        Piece capturedPiece = getCapturedPiece(pieceHash);
        capturedPiece.setCaptured(false);
        this.piece = capturedPiece;
        capturedHereList.remove(capturedPiece);
    }

    public void capturePiece(Piece capturingPiece) {
        piece.setCaptured(true);
        capturedHereList.add(piece);
        piece = capturingPiece;
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
