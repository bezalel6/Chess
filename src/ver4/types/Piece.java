package ver4.types;

import ver4.Location;

import java.util.ArrayList;

public abstract class Piece {
    public static final int COLS = 8, ROWS = 8;
    public static final int PAWN = 0, ROOK = 1, KNIGHT = 2, BISHOP = 3, QUEEN = 4, KING = 5;
    public static final int WHITE = 0, BLACK = 1;
    public static String[] colorAStringArr = {"White", "Black"};
    public static boolean isWhitePerspective = true;
    private String[] typeStringArr = {"Pawn", "Rook", "Knight", "Bishop", "Queen", "King"};
    private int worth;
    private Location pieceLoc;
    private int pieceColor;
    private int pieceType;
    private String annotation = "";
    private boolean hasMoved = false;

    public Piece(int worth, Location loc, int pieceColor, int pieceType, String annotation) {
        this.worth = worth;
        this.pieceLoc = loc;
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.annotation = annotation;
    }

    public static void setIsWhitePerspective(boolean set) {
        isWhitePerspective = set;
    }

    public void setMoved() {
        hasMoved = true;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    public boolean isWhitePerspective() {
        return isWhitePerspective;
    }

    public abstract ArrayList canMoveTo(Piece[][] pieces);

    public boolean isOnMyTeam(Piece m) {
        return pieceColor == m.pieceColor;
    }

    public boolean isOnMyTeam(int color) {
        return pieceColor == color;
    }

    public Location getLoc() {
        return pieceLoc;
    }

    public void setLoc(Location loc) {
        this.pieceLoc = loc;
    }

    public int getType() {
        return pieceType;
    }

    public boolean isWhite() {
        return pieceColor == WHITE;
    }

    public void add(ArrayList<ver4.types.Path> list, Location loc, Piece[][] pieces) {
        boolean bool = false;
        if (loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS) {
            Piece piece = pieces[loc.getRow()][loc.getCol()];
            if (piece != null && isOnMyTeam(piece))
                return;
            if (piece != null && !isOnMyTeam(piece))
                bool = true;
            list.add(new ver4.types.Path(loc, bool));
        }
    }

    public void add(ArrayList<ver4.types.Path> list, int row, int col, Piece[][] pieces) {
        boolean bool = false;
        Location loc = new Location(row, col);
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            Piece piece = pieces[row][col];
            if (piece != null && isOnMyTeam(piece))
                return;
            if (piece != null && !isOnMyTeam(piece))
                bool = true;
            list.add(new ver4.types.Path(loc, bool));
        }
    }

    public int getOtherColor() {
        return Math.abs(pieceColor - 1);
    }

    public void addAll(ArrayList<ver4.types.Path> addTo, ArrayList<Path> adding, Piece[][] pieces) {
        for (int i = 0; i < adding.size(); i++) {
            add(addTo, adding.get(i).getLoc(), pieces);
        }
    }

    public int getWorth() {
        return worth;
    }

    public int getPieceType() {
        return pieceType;
    }

    public int getPieceColor() {
        return pieceColor;
    }

    public String getColorString() {
        return colorAStringArr[pieceColor];
    }

    public boolean isInBounds(Location loc) {
        return loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "worth=" + worth +
                ", loc=" + pieceLoc +
                ", pieceColor=" + pieceColor +
                ", pieceType=" + pieceType +
                ", annotation='" + annotation + '\'' +
                '}';
    }
}
