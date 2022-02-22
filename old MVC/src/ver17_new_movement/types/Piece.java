package ver17_new_movement.types;

import ver17_new_movement.Board;
import ver17_new_movement.Location;
import ver17_new_movement.moves.Move;

import java.util.ArrayList;
import java.util.Objects;


public abstract class Piece {
    public static final int COLS = 8, ROWS = 8;
    public static final String[] PLAYER_NAMES = {"White", "Black"};
    public static final int PAWN = 0, ROOK = 1, KNIGHT = 2, BISHOP = 3, QUEEN = 4, KING = 5;
    public static final double[] WORTH = initWorthArr();
    public static final int KING_SIDE = 0, QUEEN_SIDE = 1;
    public static final int[] STARTING_ROW = new int[]{0, 7};
    int difference;
    private Location pieceLoc;
    private Location startingLoc;
    private int pieceColor;
    private int pieceType;
    private String annotation;
    private boolean hasMoved;
    private boolean eaten;
    private ArrayList<Move> movesList;

    public Piece(Location loc, int pieceColor, int pieceType, String annotation, boolean hasMoved) {
        difference = getDifference(pieceColor);
        this.pieceLoc = loc;
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.annotation = annotation;
        this.hasMoved = hasMoved;
        this.startingLoc = loc;
    }


    public Piece(Piece other) {
        difference = other.difference;
        this.pieceLoc = new Location(other.pieceLoc);
        this.pieceColor = other.pieceColor;
        this.pieceType = other.pieceType;
        this.annotation = other.annotation;
        this.hasMoved = other.hasMoved;
        movesList = new ArrayList<>();
        if (hasMoved)
            for (Move move : other.movesList) {
                movesList.add(new Move(move, false));
            }
    }

    private static double[] initWorthArr() {
        double[] arr = new double[6];
        arr[PAWN] = 1;
        arr[ROOK] = 5;
        arr[KING] = 200;
        arr[KNIGHT] = 3.1;
        arr[BISHOP] = 3.2;
        arr[QUEEN] = 9;
        return arr;
    }

    public static int getDifference(int player) {
        return player == Player.WHITE ? 1 : -1;
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

    public static Piece promotePiece(Piece piece, int promotingTo) {
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

    public static boolean isLocInMovesList(ArrayList<Move> moves, Location loc) {
        for (Move move : moves)
            if (move.getMovingTo().equals(loc))
                return true;
        return false;
    }

    public boolean isEaten() {
        return eaten;
    }

    public void setEaten(boolean eaten) {
        this.eaten = eaten;
    }

    //region not important
    public int getDifference() {
        return difference;
    }

    public Location getStartingLoc() {
        return startingLoc;
    }

    public int getOpponent() {
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
//        this.actualMatLoc = Location.convertToMatLoc(loc);
        this.pieceLoc.setLoc(loc);
    }

    public boolean isWhite() {
        return pieceColor == Player.WHITE;
    }


    //endregion
    public ArrayList<Move> canMoveTo(Board board) {
        ArrayList<Move> ret = new ArrayList();
        if (isEaten()) return ret;
        checkLegal(ret, getPseudoMoves(board), board);
        return ret;
    }


    public ArrayList<Move> getPseudoMoves(Board board) {
        ArrayList<Move> ret = new ArrayList();
        if (isEaten()) return ret;
        generatePseudoMoves(board).forEach(list -> checkBoundsAndHits(ret, list, board));
        return ret;
    }

    public abstract ArrayList<ArrayList<Move>> generatePseudoMoves(Board board);


    private void checkLegal(ArrayList<Move> addTo, ArrayList<Move> currentlyAdding, Board board) {
        for (Move move : currentlyAdding) {
            board.applyMove(move);
            if (!board.isInCheck(getPieceColor()))
                addTo.add(move);
            board.undoMove(move);
        }
    }

    private void checkBoundsAndHits(ArrayList<Move> addTo, ArrayList<Move> currentlyAdding, Board board) {
        for (Move move : currentlyAdding) {
            Location loc = move.getMovingTo();
            if (!isInBounds(loc))
                return;
            Piece destination = board.getPiece(loc);
            if (destination != null) {
                if (isOnMyTeam(destination) || (this instanceof Pawn && !move.isCapturing()))
                    return;
                move.setCapturing(true);
                addTo.add(move);
                return;
            }
            addTo.add(move);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return pieceColor == piece.pieceColor && pieceType == piece.pieceType && hasMoved == piece.hasMoved && pieceLoc.equals(piece.pieceLoc) && Objects.equals(annotation, piece.annotation);
    }

    public double getWorth() {
        return WORTH[pieceType];
    }

    public int getPieceType() {
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


    public static class Player {
        public static final int WHITE = 0, BLACK = 1;

        public static int getOtherColor(int currentPlayer) {
            return Math.abs((currentPlayer - 1) * -1);
        }
    }

}
