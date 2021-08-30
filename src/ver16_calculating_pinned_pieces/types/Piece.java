package ver16_calculating_pinned_pieces.types;

import ver16_calculating_pinned_pieces.Board;
import ver16_calculating_pinned_pieces.Location;
import ver16_calculating_pinned_pieces.moves.Move;

import java.util.ArrayList;
import java.util.Objects;


public abstract class Piece {
    public static final Object P = new Object();
    public static final int COLS = 8, ROWS = 8;
    public static final String[] PLAYER_NAMES = {"White", "Black"};
    private double worth;
    private Location pieceLoc;
    private Location actualMatLoc;
    private int pieceColor;
    private PieceTypes pieceType;
    private String annotation;
    private boolean hasMoved;
    private ArrayList<Move> movesList;
    private ArrayList<Piece> piecesBlockedByMe;
    private ArrayList<Piece> piecesBlockingMe;
    private ArrayList<Skewer> skewers;

    public Piece(double worth, Location loc, int pieceColor, PieceTypes pieceType, String annotation, boolean hasMoved) {
        this.worth = worth;
        this.pieceLoc = loc;
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.annotation = annotation;
        this.hasMoved = hasMoved;
        this.actualMatLoc = loc;
        piecesBlockingMe = new ArrayList<>();
        piecesBlockedByMe = new ArrayList<>();
        skewers = new ArrayList<>();

    }

    public Piece(Piece other) {
        this.worth = other.worth;
        this.pieceLoc = new Location(other.pieceLoc);
        this.actualMatLoc = new Location(other.actualMatLoc);
        this.pieceColor = other.pieceColor;
        this.pieceType = other.pieceType;
        this.annotation = other.annotation;
        this.hasMoved = other.hasMoved;
        movesList = new ArrayList<>();
        if (hasMoved)
            for (Move move : other.movesList) {
                movesList.add(new Move(move, false));
            }
        piecesBlockingMe = new ArrayList<>(other.piecesBlockingMe);
        piecesBlockedByMe = new ArrayList<>(other.piecesBlockedByMe);
        skewers = new ArrayList<>();
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

    public static Piece promotePiece(Piece piece, PieceTypes promotingTo) {
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

    public static Piece createPieceFromFen(char c, Location pieceLocation) {
        if (!Character.isLetter(c)) return null;
        int pieceColor = Character.isUpperCase(c) ? Player.WHITE : Player.BLACK;
        switch (Character.toLowerCase(c)) {
            case 'p':
                return new Pawn(pieceLocation, pieceColor, false);
            case 'r':
                return new Rook(pieceLocation, pieceColor, false);
            case 'n':
                return new Knight(pieceLocation, pieceColor, false);
            case 'b':
                return new Bishop(pieceLocation, pieceColor, false);
            case 'q':
                return new Queen(pieceLocation, pieceColor, false);
            case 'k':
                return new King(pieceLocation, pieceColor, false);
        }
        return null;
    }

    public ArrayList<Skewer> getSkewers() {
        return skewers;
    }

    public void addSkewer(Skewer skewer) {
        skewers.add(skewer);
    }

    public int getOtherColor() {
        return pieceColor == Player.WHITE ? Player.BLACK : Player.WHITE;
    }

    public int getPieceColor() {
        return pieceColor;
    }

    public void setMoved(Move move) {
        if (movesList == null || movesList.isEmpty()) {
            movesList = new ArrayList<>();
            movesList.add(move);
            hasMoved = true;
        } else if (movesList.contains(move)) {
            movesList.remove(move);
            hasMoved = !movesList.isEmpty();
        }

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

    public abstract ArrayList<Move> canMoveTo(Board board);

    public boolean isOnMyTeam(Piece m) {
        return pieceColor == m.pieceColor;
    }

    public boolean isOnMyTeam(int player) {
        return pieceColor == player;
    }

    public Location getLoc() {
        return pieceLoc;
    }

    public void setLoc(Location loc) {
        this.actualMatLoc = Location.convertToMatLoc(loc);
        this.pieceLoc = loc;
    }

    public PieceTypes getType() {
        return pieceType;
    }

    public boolean isWhite() {
        return pieceColor == Player.WHITE;
    }

    public Move add(ArrayList<Move> list, Location loc, Board board) {
        return add(list, loc.getRow(), loc.getCol(), board);
    }

    private void addBlockingPiece(Piece blockingPiece) {
        piecesBlockingMe.add(blockingPiece);
        blockingPiece.piecesBlockedByMe.add(this);
    }

    public Move add(ArrayList<Move> list, int row, int col, Board board) {
        boolean isCapturing = false;
        Location loc = new Location(row, col);
        if (isInBounds(loc)) {
            Piece piece = board.getPiece(loc);
            if (piece != null) {
                addBlockingPiece(piece);
//                if (isOnMyTeam(piece)) {
                return new Move();
//                }
//            else isCapturing = true;
            }

            Move newMove = new Move(pieceLoc, loc, isCapturing, board);
            list.add(newMove);
            return isCapturing ? null : newMove;

        }
        return null;
    }

    public void xRay(Piece skeweredPiece, Piece pieceBehind) {
        Skewer skewer = new Skewer();
        try {
            skewer = new Skewer(skeweredPiece, this, pieceBehind);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
        skeweredPiece.addSkewer(skewer);
    }

    public void eaten() {
//        piecesBlockedByMe
//        piecesBlockingMe = new ArrayList<>();
    }

    public Move add(ArrayList<Move> list, Move move, Board board) {
        boolean isCapturing = move.isCapturing();
        Location loc = new Location(move.getMovingTo());
        if (isInBounds(loc)) {
            Piece piece = board.getPiece(loc);
            if (piece != null) {
                addBlockingPiece(piece);
                if (isOnMyTeam(piece)) {
                    return null;
                } else isCapturing = true;
            }
            move.setCapturing(isCapturing);
            list.add(move);
            return move;

        }
        return null;
    }

    public ArrayList<Piece> getPiecesBlockedByMe() {
        return piecesBlockedByMe;
    }

    public void setPiecesBlockedByMe(ArrayList<Piece> piecesBlockedByMe) {
        this.piecesBlockedByMe = piecesBlockedByMe;
    }

    public ArrayList<Piece> getPiecesBlockingMe() {
        return piecesBlockingMe;
    }

    public void setPiecesBlockingMe(ArrayList<Piece> piecesBlockingMe) {
        this.piecesBlockingMe = piecesBlockingMe;
    }

    public void addAll(ArrayList<Move> addTo, ArrayList<Move> adding, Board board) {
        for (Move move : adding) {
            add(addTo, move, board);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return worth == piece.worth && pieceColor == piece.pieceColor && pieceType == piece.pieceType && hasMoved == piece.hasMoved && pieceLoc.equals(piece.pieceLoc) && Objects.equals(annotation, piece.annotation);
    }

    public double getWorth() {
        return worth;
    }

    public PieceTypes getPieceType() {
        return pieceType;
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
                "pieceLoc=" + pieceLoc +
                ", actualMatLoc=" + actualMatLoc.getNumValues() +
                ", pieceColor=" + pieceColor +
                ", pieceType=" + pieceType +
                ", movesList=" + movesList +
                '}';
    }

    public void deleteMove() {
        if (movesList != null && !movesList.isEmpty())
            movesList.remove(movesList.size() - 1);
        hasMoved = !movesList.isEmpty();
    }

    public enum PieceTypes {PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING}

    public static class Player {
        public static final int WHITE = 0, BLACK = 1;

        public static int getOtherColor(int currentPlayer) {
            return Math.abs((currentPlayer - 1) * -1);
        }
    }

    public class Skewer {
        private Piece skeweredPiece, skeweringPiece, pieceBehind;

        public Skewer() {
        }

        public Skewer(Piece skeweredPiece, Piece skeweringPiece, Piece pieceBehind) throws Exception {
            if (skeweredPiece == null || skeweringPiece == null || pieceBehind == null)
                throw new Exception("one of the skewering elements is null " + skeweredPiece + " " + skeweringPiece + " " + pieceBehind);
            this.skeweredPiece = skeweredPiece;
            this.skeweringPiece = skeweringPiece;
            this.pieceBehind = pieceBehind;
        }

        public Piece getSkeweredPiece() {
            return skeweredPiece;
        }

        public void setSkeweredPiece(Piece skeweredPiece) {
            this.skeweredPiece = skeweredPiece;
        }

        public Piece getSkeweringPiece() {
            return skeweringPiece;
        }

        public void setSkeweringPiece(Piece skeweringPiece) {
            this.skeweringPiece = skeweringPiece;
        }

        public Piece getPieceBehind() {
            return pieceBehind;
        }

        public void setPieceBehind(Piece pieceBehind) {
            this.pieceBehind = pieceBehind;
        }

        @Override
        public String toString() {
            return "Skewer{" +
                    "skeweredPiece=" + skeweredPiece +
                    ", skeweringPiece=" + skeweringPiece +
                    ", pieceBehind=" + pieceBehind +
                    '}';
        }
    }
//
//    public enum Player {
//        WHITE, BLACK;
//
//        public static Player getOtherColor(Player clr) {
//            return clr == Player.WHITE ? Player.BLACK : Player.WHITE;
//        }
//    }
}
