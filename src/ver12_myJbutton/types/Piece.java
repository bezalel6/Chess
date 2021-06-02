package ver12_myJbutton.types;

import ver12_myJbutton.Board;
import ver12_myJbutton.Location;
import ver12_myJbutton.moves.Move;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Piece {
    public static final int COLS = 8, ROWS = 8;
    private int worth;
    private Location pieceLoc;
    private Player pieceColor;
    private types pieceType;
    private String annotation = "";
    private boolean hasMoved;

    //Starting from position
    public Piece(int worth, Location loc, Player pieceColor, types pieceType, String annotation, boolean hasMoved) {
        this.worth = worth;
        this.pieceLoc = loc;
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.annotation = annotation;
        this.hasMoved = hasMoved;
    }

    public Piece(Piece other) {
        this.worth = other.worth;
        this.pieceLoc = new Location(other.pieceLoc);
        this.pieceColor = other.pieceColor;
        this.pieceType = other.pieceType;
        this.annotation = other.annotation;
        this.hasMoved = other.hasMoved;
    }

    public static Piece copyPiece(Piece other) {
        if (other == null) return null;
        if (other instanceof Knight) {
            return new Knight(other);
        } else if (other instanceof Bishop) {
            return new Bishop(other);
        } else if (other instanceof Rook) {
            return new Rook(other);
        } else if (other instanceof Queen) {
            return new Queen(other);
        } else if (other instanceof King) {
            return new King(other);
        } else {
            return new Pawn(other);
        }
    }

    public static Piece promotePiece(Piece piece, types promotingTo) {
        if (piece == null) return null;

        switch (promotingTo) {
            case QUEEN:
                return new Queen(piece.getLoc(), piece.pieceColor, piece.hasMoved);
            case ROOK:
                return new Rook(piece.getLoc(), piece.pieceColor, piece.hasMoved);
            case KNIGHT:
                return new Knight(piece.getLoc(), piece.pieceColor, piece.hasMoved);
            case BISHOP:
                return new Bishop(piece.getLoc(), piece.pieceColor, piece.hasMoved);
            default:
                return null;
        }
    }

    public void setMoved(Move move) {
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

    public abstract ArrayList canMoveTo(Board board);

    public boolean isOnMyTeam(Piece m) {
        return pieceColor == m.pieceColor;
    }

    public boolean isOnMyTeam(Player player) {
        return pieceColor == player;
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
        return pieceColor == Player.WHITE;
    }

    public Move add(ArrayList<Move> list, Location loc, Board board) {
        return add(list, loc.getRow(), loc.getCol(), board);
    }

    public Move add(ArrayList<Move> list, int row, int col, Board board) {
        boolean isCapturing = false;
        Location loc = new Location(row, col);
        if (isInBounds(loc)) {
            Piece piece = board.getPiece(loc);
            if (piece != null && isOnMyTeam(piece))
                return null;
            if (piece != null && !isOnMyTeam(piece))
                isCapturing = true;
            Move newMove = new Move(pieceLoc, loc, isCapturing, board);
            list.add(newMove);
            return newMove;

        }
        return null;
    }

    public Move add(ArrayList<Move> list, Move move, Board board) {
        boolean isCapturing = false;
        Location loc = move.getMovingTo();
        if (isInBounds(loc)) {
            Piece piece = board.getPiece(loc);
            if (piece != null && isOnMyTeam(piece))
                return null;
            if (piece != null && !isOnMyTeam(piece))
                isCapturing = true;
            list.add(move);
            return list.get(list.size() - 1);

        }
        return null;
    }

    public void addAll(ArrayList<Move> addTo, ArrayList<Move> adding, Board board) {
        for (Move move : adding) {
            add(addTo, move, board);
        }
    }

    public Player getOtherColor() {
        return pieceColor == Player.WHITE ? Player.BLACK : Player.WHITE;
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

    public Player getPieceColor() {
        return pieceColor;
    }

    public boolean isInBounds(Location loc) {
        return loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS;
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "loc=" + pieceLoc +
                ", pieceColor=" + pieceColor +
                ", pieceType=" + pieceType +
                ", annotation='" + annotation + '\'' +
                '}';
    }

    public enum types {PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING}

    public enum Player {
        WHITE, BLACK;

        public static Player getOtherColor(Player clr) {
            return clr == Player.WHITE ? Player.BLACK : Player.WHITE;
        }
    }
}
