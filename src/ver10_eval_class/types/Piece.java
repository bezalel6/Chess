package ver10_eval_class.types;

import ver10_eval_class.Location;
import ver10_eval_class.Move;
import ver10_eval_class.Positions;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Piece {
    public static final int COLS = 8, ROWS = 8;
    private int worth;
    private Location pieceLoc;
    private colors pieceColor;
    private types pieceType;
    private String annotation = "";
    private boolean hasMoved;


    //Starting from position
    public Piece(int worth, Location loc, colors pieceColor, types pieceType, String annotation, boolean hasMoved) {
        this.worth = worth;
        this.pieceLoc = loc;
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.annotation = annotation;
        this.hasMoved = hasMoved;
    }

    public Piece copy(Piece otherPiece) {
        return Positions.strToPiece(Positions.pieceToStr(otherPiece, otherPiece.hasMoved));

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

    public abstract ArrayList canMoveTo(Piece[][] pieces);

    public boolean isOnMyTeam(Piece m) {
        return pieceColor == m.pieceColor;
    }

    public boolean isOnMyTeam(colors color) {
        return pieceColor == color;
    }

    public Location getLoc() {
        return pieceLoc;
    }

    public void setLoc(Location loc) {
        this.pieceLoc = loc;
    }

    public types getType() {
        return pieceType;
    }

    public boolean isWhite() {
        return pieceColor == colors.WHITE;
    }

    public Move add(ArrayList<Move> list, Location loc, Piece[][] pieces) {
        boolean isCapturing = false;
        if (loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS) {
            Piece piece = pieces[loc.getRow()][loc.getCol()];
            if (piece != null && isOnMyTeam(piece))
                return null;
            if (piece != null && !isOnMyTeam(piece))
                isCapturing = true;
            list.add(new Move(this, loc, isCapturing));
            return list.get(list.size() - 1);

        }
        return null;
    }

    public Move add(ArrayList<Move> list, int row, int col, Piece[][] pieces) {
        boolean isCapturing = false;
        Location loc = new Location(row, col);
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            Piece piece = pieces[row][col];
            if (piece != null && isOnMyTeam(piece))
                return null;
            if (piece != null && !isOnMyTeam(piece))
                isCapturing = true;
            list.add(new Move(this, loc, isCapturing));
            return list.get(list.size() - 1);

        }
        return null;
    }

    public Move add(ArrayList<Move> list, Move move, Piece[][] pieces) {
        boolean isCapturing = false;
        Location loc = move.getMovingFrom();
        if (loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS) {
            Piece piece = pieces[loc.getRow()][loc.getCol()];
            if (piece != null && isOnMyTeam(piece))
                return null;
            if (piece != null && !isOnMyTeam(piece))
                isCapturing = true;
            list.add(new Move(this, loc, isCapturing));
            return list.get(list.size() - 1);

        }
        return null;
    }

    public void addAll(ArrayList<Move> addTo, ArrayList<Move> adding, Piece[][] pieces) {
        for (Move move : adding) {
            add(addTo, move, pieces);
        }
    }

    public colors getOtherColor() {
        return pieceColor == colors.WHITE ? colors.BLACK : colors.WHITE;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return worth == piece.worth && pieceColor == piece.pieceColor && pieceType == piece.pieceType && hasMoved == piece.hasMoved && pieceLoc.equals(piece.pieceLoc) && Objects.equals(annotation, piece.annotation);
    }

    public int getWorth() {
        return worth;
    }

    public types getPieceType() {
        return pieceType;
    }

    public colors getPieceColor() {
        return pieceColor;
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

    public enum types {PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING}

    public enum colors {
        WHITE, BLACK;

        public static colors getOtherColor(colors clr) {
            return clr == colors.WHITE ? colors.BLACK : colors.WHITE;
        }
    }
}
