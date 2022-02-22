package ver2.types;

import ver2.Controller;
import ver2.Location;
import ver2.Model;

import java.util.ArrayList;

public abstract class Piece {
    public ArrayList<Path> legalMoves;

    ;
    int worth;

    ;
    Location pieceLoc, firstLoc;
    colors pieceColor;
    types pieceType;
    String annotation = "";
    public Piece(int worth, Location loc, colors pieceColor, types pieceType, String annotation) {
        this.worth = worth;
        this.pieceLoc = firstLoc = loc;
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.annotation = annotation;
    }

    public boolean hasMoved() {
        return !firstLoc.isEqual(getLoc());
    }

    public void updateLegalMoves() {
        legalMoves = canMoveTo();
    }

    public abstract ArrayList canMoveTo();

    public boolean isOnMyTeam(Piece m) {
        return pieceColor.equals(m.pieceColor);
    }

    public Location getLoc() {
        return pieceLoc;
    }

    public void setLoc(Location loc) {
        this.pieceLoc = loc;
    }

    public String getType() {
        return pieceType.name();
    }

    boolean isWhite() {
        return pieceColor == colors.White;
    }

    public void add(ArrayList<Path> list, Location loc) {
        boolean bool = false;
        Piece m = Model.getPiece(loc);
        if (m != null && isOnMyTeam(m))
            return;
        if (m != null && !isOnMyTeam(m))
            bool = true;
        list.add(new Path(loc, bool));
        checkLegal(list);
    }

    public void add(ArrayList<Path> list, int row, int col) {
        boolean bool = false;
        Location loc = new Location(row, col);
        Piece m = Model.getPiece(loc);
        if (m != null && isOnMyTeam(m))
            return;
        if (m != null && !isOnMyTeam(m))
            bool = true;
        list.add(new Path(loc, bool));
        checkLegal(list);
    }

    public void addAll(ArrayList<Path> addTo, ArrayList<Path> adding) {
        for (int i = 0; i < adding.size(); i++) {
            add(addTo, adding.get(i).getLoc());
        }
    }

    public colors getPieceColor() {
        return pieceColor;
    }

    public String getColorString() {
        return pieceColor.name();
    }

    public void checkLegal(ArrayList<Path> list) {
        for (int i = 0; i < list.size(); i++) {
            Location loc = list.get(i).getLoc();
            if (loc.getRow() >= Controller.ROWS || loc.getCol() >= Controller.COLS || loc.getRow() < 0 || loc.getCol() < 0)
                list.remove(i);
            Piece m = Model.getPiece(loc);
            if (m != null && isOnMyTeam(m))
                list.remove(i);
            Location storeLoc = getLoc();
            setLoc(loc);
            if (Model.isThreatened(Model.getKing(getPieceColor()))) {
                list.remove(i);
            }
            setLoc(storeLoc);
        }
    }

    @Override
    public String toString() {
        return "master{" +
                "worth=" + worth +
                ", loc=" + pieceLoc +
                ", pieceColor=" + pieceColor +
                ", pieceType=" + pieceType +
                ", annotation='" + annotation + '\'' +
                '}';
    }

    public enum types {Pawn, Rook, Knight, Bishop, Queen, King}

    public enum colors {White, Black}
}
