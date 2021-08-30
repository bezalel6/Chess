package ver18_icon_manager.types;

import ver18_icon_manager.Board;
import ver18_icon_manager.Location;

import java.util.ArrayList;


public class Pin {
    public static final int PINNING = 0, PINNED = 1;
    private Piece pinnedPiece, pinningPiece, pinnedTo;
    private ArrayList<Location> path;
    private Board board;

    public Pin(Piece pinnedPiece, Piece pinningPiece, Piece pinnedTo, ArrayList<Location> path, Board board) {
        if (pinnedPiece == null || pinningPiece == null || pinnedTo == null)
            System.out.println("one of the skewering elements is null " + pinnedPiece + " " + pinningPiece + " " + pinnedTo);
        this.pinnedPiece = pinnedPiece;
        this.pinningPiece = pinningPiece;
        this.pinnedTo = pinnedTo;
        this.path = path;
        this.board = board;
    }

//    public static ArrayList<Location> getLegalPath(Piece piece) {
//        if (piece.getPins()[PINNED].isEmpty())
//            return null;
//
//        ArrayList<Pin> piecePin = piece.getPins()[PINNED];
//        ArrayList<Location> ret = piecePin.get(0).getPath();
//        ArrayList<Location> del = new ArrayList<>();
//        for (int i = 1, piecePinSize = piecePin.size(); i < piecePinSize; i++) {
//            Pin pin = piecePin.get(i);
//            for (Location loc : ret) {
//                if (!pin.path.contains(loc))
//                    del.add(loc);
//            }
//        }
//        ret.removeAll(del);
//        return ret;
//    }

    public Board getBoard() {
        return board;
    }

    public ArrayList<Location> getPath() {
        return new ArrayList<>(path);
    }

    public void setPath(ArrayList<Location> path) {
        this.path = path;
    }

    public Piece getPinnedPiece() {
        return pinnedPiece;
    }

    public void setPinnedPiece(Piece pinnedPiece) {
        this.pinnedPiece = pinnedPiece;
    }

    public Piece getPinningPiece() {
        return pinningPiece;
    }

    public void setPinningPiece(Piece pinningPiece) {
        this.pinningPiece = pinningPiece;
    }

    public Piece getPinnedTo() {
        return pinnedTo;
    }

    public void setPinnedTo(Piece pinnedTo) {
        this.pinnedTo = pinnedTo;
    }

    public void refresh() {

    }

    @Override
    public String toString() {
        return "Skewer{" +
                "skeweredPiece=" + pinnedPiece +
                ", skeweringPiece=" + pinningPiece +
                ", pieceBehind=" + pinnedTo +
                '}';
    }
}