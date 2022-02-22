package ver20_optimization.types;

import ver20_optimization.Error;
import ver20_optimization.model_classes.Board;
import ver20_optimization.Location;
import ver20_optimization.moves.Move;

import java.util.ArrayList;


public abstract class Piece {
    public static final int COLS = 8, ROWS = 8;
    public static final String[] PLAYER_NAMES = {"White", "Black"};
    public static final String[] PIECES_NAMES = {"Pawn", "Rook", "Knight", "Bishop", "Queen", "King"};
    public static final int NUM_OF_PLAYERS = 2, NUM_OF_PIECE_TYPES = 6;
    public static final int PAWN = 0, ROOK = 1, KNIGHT = 2, BISHOP = 3, QUEEN = 4, KING = 5;
    public static final double[] WORTH = initWorthArr();
    public static final int KING_SIDE = 0, QUEEN_SIDE = 1;
    public static final int[] STARTING_ROW = {0, 7};
    public static final int[] CAN_PROMOTE_TO = {ROOK, KNIGHT, BISHOP, QUEEN};
    public static final int[] MOST_LIKELY_TO_CHECK = {1, 2, 3, 4, 5, -1};
    private final Location startingLoc;
    private final int pieceColor;
    private final int pieceType;
    int difference;
    private Location pieceLoc;
    private String annotation;
    private boolean captured;

    public Piece(Location loc, int pieceColor, int pieceType, String annotation) {
        this.difference = getDifference(pieceColor);
        this.pieceLoc = new Location(loc);
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.annotation = annotation;
        this.startingLoc = new Location(loc);
    }


    public Piece(Piece other) {
        this.difference = other.difference;
        this.pieceLoc = new Location(other.pieceLoc);
        this.startingLoc = new Location(other.startingLoc);
        this.pieceColor = other.pieceColor;
        this.pieceType = other.pieceType;
        this.annotation = other.annotation;
        this.captured = other.captured;
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
        if (piece == null) Error.error("piece is null");
        Location startingLoc = piece.startingLoc;
        Piece ret = null;
        switch (promotingTo) {
            case QUEEN: {
                ret = new Queen(startingLoc, piece.pieceColor);
                break;
            }
            case ROOK: {
                ret = new Rook(startingLoc, piece.pieceColor);
                break;
            }
            case KNIGHT: {
                ret = new Knight(startingLoc, piece.pieceColor);
                break;
            }
            case BISHOP: {
                ret = new Bishop(startingLoc, piece.pieceColor);
                break;
            }
        }
        ret.setLoc(piece.pieceLoc);
        return ret;
    }

    public static Piece createPieceFromFen(char c, Location pieceLocation) {
        if (!Character.isLetter(c)) Error.error("Character was not a letter");
        int pieceColor = Character.isUpperCase(c) ? Player.WHITE : Player.BLACK;
        switch (Character.toLowerCase(c)) {
            case 'p':
                return new Pawn(pieceLocation, pieceColor);
            case 'r':
                return new Rook(pieceLocation, pieceColor);
            case 'n':
                return new Knight(pieceLocation, pieceColor);
            case 'b':
                return new Bishop(pieceLocation, pieceColor);
            case 'q':
                return new Queen(pieceLocation, pieceColor);
            case 'k':
                return new King(pieceLocation, pieceColor);
        }
        return null;
    }

    public static boolean isInBounds(Location loc) {
        return loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS;
    }

    public static boolean addMove(Move move, Piece piece, ArrayList<Move> list, Board board) {
        Location movingTo = move.getMovingTo();
        if (!isInBounds(movingTo))
            return false;
        Piece otherPiece = board.getPiece(movingTo);
        if (otherPiece == null) {
            list.add(move);
            return true;
        }
        if (!otherPiece.isOnMyTeam(piece)) {
            if (otherPiece instanceof King)
                move.setCheck(true);
            move.setCapturing(otherPiece.hashCode());
            list.add(move);
        }
        return false;
    }

    /**
     * @param move
     * @param piece
     * @param empty - the square has to be by the empty value (true - square has to be empty. false - square has to be not empty)
     * @param list
     * @param board
     * @return
     */
    public static boolean addMove(Move move, Piece piece, boolean empty, ArrayList<Move> list, Board board) {
        Location movingTo = move.getMovingTo();
        if (!isInBounds(movingTo))
            return false;
        Piece otherPiece = board.getPiece(movingTo);
        if (otherPiece == null && empty) {
            list.add(move);
            return true;
        } else if (!empty && otherPiece != null && !otherPiece.isOnMyTeam(piece)) {
            if (otherPiece instanceof King)
                move.setCheck(true);
            move.setCapturing(otherPiece.hashCode());
            list.add(move);
        }
        return false;
    }

    public static boolean addMove(Location movingTo, Piece piece, ArrayList<Move> list, Board board) {
        if (isInBounds(movingTo))
            return addMove(new Move(piece.pieceLoc, movingTo, board), piece, list, board);
        return false;
    }

    public static boolean addSingleMove(Location movingTo, Piece piece, boolean empty, ArrayList<ArrayList<Move>> list, Board board) {
        return addSingleMove(new Move(piece.pieceLoc, movingTo, board), piece, empty, list, board);
    }

    public static boolean addSingleMove(Move move, Piece piece, boolean empty, ArrayList<ArrayList<Move>> list, Board board) {
        ArrayList<Move> l2 = new ArrayList<>();
        boolean ret = addMove(move, piece, empty, l2, board);
        if (!l2.isEmpty())
            list.add(l2);
        return ret;
    }

    public static boolean addSingleMove(Location movingTo, Piece piece, ArrayList<ArrayList<Move>> list, Board board) {
        return addSingleMove(new Move(piece.pieceLoc, movingTo, board), piece, list, board);
    }

    public static boolean addSingleMove(Move move, Piece piece, ArrayList<ArrayList<Move>> list, Board board) {
        ArrayList<Move> l2 = new ArrayList<>();
        boolean ret = addMove(move, piece, l2, board);
        if (!l2.isEmpty())
            list.add(l2);
        return ret;
    }

    public static boolean addMove(int r, int c, Piece piece, ArrayList<Move> list, Board board) {
        return addMove(new Location(r, c), piece, list, board);
    }

    static boolean batchCheckBounds(Location[] list) {
        for (Location loc : list) {
            if (!isInBounds(loc))
                return false;
        }
        return true;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    //region not important
    public int getDifference() {
        return difference;
    }

    public Location getStartingLoc() {
        return startingLoc;
    }

    public int getOpponent() {
        return Player.getOtherColor(pieceColor);
    }

    public int getPieceColor() {
        return pieceColor;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public boolean getHasMoved() {
        return pieceLoc.getRow() != STARTING_ROW[pieceColor] || !pieceLoc.equals(startingLoc);
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
        pieceLoc = new Location(loc);
    }

    public boolean isWhite() {
        return pieceColor == Player.WHITE;
    }

    //endregion
    public ArrayList<Move> canMoveTo(Board board) {
        ArrayList<Move> ret = new ArrayList();
        if (isCaptured()) return ret;
        ArrayList<ArrayList<Move>> generatePseudoMoves = generatePseudoMoves(board);
        for (ArrayList<Move> moves : generatePseudoMoves) {
            checkLegal(ret, moves, board);
        }
        return ret;
    }

    public ArrayList<Move> getPseudoMovesList(Board board) {
        ArrayList<Move> ret = new ArrayList();
        if (isCaptured()) return ret;

        //todo o(no) ↓
        generatePseudoMoves(board).forEach(ret::addAll);
        return ret;
    }

    abstract ArrayList<ArrayList<Move>> generatePseudoMoves(Board board);

    void checkLegal(ArrayList<Move> addTo, ArrayList<Move> currentlyAdding, Board board) {
        for (Move move : currentlyAdding) {
            addIfLegalMove(addTo, move, board);
        }
    }

    void addIfLegalMove(ArrayList<Move> list, Move move, Board board) {
        if (checkLegalMove(move, board))
            list.add(move);
    }

    boolean checkLegalMove(Move move, Board board) {
        board.applyMove(move);
        boolean ret = !board.isInCheck(pieceColor);
        board.undoMove(move);
        return ret;
    }

    @Override
    public int hashCode() {
        return startingLoc.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return captured == piece.captured && pieceColor == piece.pieceColor && pieceType == piece.pieceType && pieceLoc.equals(piece.pieceLoc) && startingLoc.equals(piece.startingLoc);
    }

    public double getWorth() {
        return WORTH[pieceType];
    }

    public int getPieceType() {
        return pieceType;
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "pieceLoc=" + pieceLoc +
                ", pieceColor=" + pieceColor +
                ", worth=" + getWorth() +
                ", eaten=" + captured +
                '}';
    }

    public static class Player {
        public static final int WHITE = 0, BLACK = 1;

        public static int getOtherColor(int currentPlayer) {
            return Math.abs((currentPlayer - 1) * -1);
        }
    }

}
