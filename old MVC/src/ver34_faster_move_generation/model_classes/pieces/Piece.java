package ver34_faster_move_generation.model_classes.pieces;

import ver34_faster_move_generation.Controller;
import ver34_faster_move_generation.Location;
import ver34_faster_move_generation.MyError;
import ver34_faster_move_generation.Player;
import ver34_faster_move_generation.model_classes.Board;
import ver34_faster_move_generation.model_classes.moves.Move;
import ver34_faster_move_generation.model_classes.moves.PieceMoves;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public abstract class Piece {
    public static final int NUM_OF_PLAYERS = 2, NUM_OF_PIECE_TYPES = 6;
    public static final int PAWN = 0, ROOK = 1, KNIGHT = 2, BISHOP = 3, QUEEN = 4, KING = 5;
    public static final String[] PIECES_NAMES = {"Pawn", "Rook", "Knight", "Bishop", "Queen", "King"};
    public static final double[] WORTH = initWorthArr();
    public static final int[] MINOR_PIECES = {BISHOP, KNIGHT};
    public static final int[] MAJOR_PIECES = {QUEEN, ROOK};
    public static final int[] STARTING_ROW = {7, 0};
    public static final int[] CAN_PROMOTE_TO = {KNIGHT, ROOK, BISHOP, QUEEN};
    public static final int[] PIECES_TYPES = {PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING};
    public static final int[] UNIQUE_MOVES_PIECE_TYPES = {ROOK, KNIGHT, BISHOP, PAWN, KING};
    public static final int[] MOST_LIKELY_TO_CHECK = {1, 2, 3, 4, 5, -1};
    private static final String[] COLORLESS_PIECES_FENS = {"P", "R", "N", "B", "Q", "K"};
    public static final String[][] PIECES_FENS = createPiecesFens();
    private static final String[] PIECES_ICONS_COLORS = {"\u001B[37m", "\u001B[30m"};
    private static final String[] COLORLESS_PIECES_ICONS = {"♙", "♖", "♘", "♗", "♕", "♔"};
    public static final String[][] PIECES_ICONS = createPiecesIcons();

    private final PieceMoves pieceMoves;
    private final int pieceColor;
    private final int pieceType;
    private Location startingLoc;
    private int difference;
    private Location pieceLoc;
    private String annotation;
    private boolean captured;

    public Piece(Location loc, int pieceColor, int pieceType, String annotation) {
        this.difference = getDifference(pieceColor);
        this.pieceMoves = new PieceMoves(this);
        this.pieceLoc = new Location(loc);
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.annotation = annotation;
        this.startingLoc = new Location(loc);
    }

    //    }
    public static int getStartingRow(int player) {
        return STARTING_ROW[player];
    }

    private static String[][] createPiecesFens() {
        String[][] ret = new String[NUM_OF_PLAYERS][NUM_OF_PIECE_TYPES];
        for (int i = 0; i < NUM_OF_PLAYERS; i++) {
            ret[i] = Arrays.copyOf(COLORLESS_PIECES_FENS, COLORLESS_PIECES_FENS.length);
            if (i != Player.WHITE) {
                String[] strings = ret[i];
                for (int j = 0, stringsLength = strings.length; j < stringsLength; j++) {
                    String str = strings[j];
                    strings[j] = str.toLowerCase(Locale.ROOT);
                }
            }
        }
        return ret;
    }

    private static String[][] createPiecesIcons() {
        String[][] ret = new String[NUM_OF_PLAYERS][NUM_OF_PIECE_TYPES];
        String reset = "\u001B[0m";
        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret[i].length; j++) {
                ret[i][j] = PIECES_ICONS_COLORS[i] + COLORLESS_PIECES_ICONS[j] + reset;
            }
        }
        return ret;
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
        return player == Player.WHITE ? -1 : 1;
    }

    public static Piece copyPiece(Piece other) {
        if (other == null) return null;

        switch (other.pieceType) {
            case PAWN:
                return new Pawn(other);
            case BISHOP:
                return new Bishop(other);
            case KNIGHT:
                return new Knight(other);
            case ROOK:
                return new Rook(other);
            case QUEEN:
                return new Queen(other);
            case KING:
                return new King(other);
            default:
                MyError.error("wrong piece type");
                return null;
        }
    }

    public static Piece promotePiece(Piece piece, int promotingTo) {
        assert piece != null;
        assert Arrays.stream(CAN_PROMOTE_TO).anyMatch(num -> num == promotingTo);
        Location startingLoc = piece.startingLoc;
        Piece ret = createPieceFromFen(PIECES_FENS[piece.pieceColor][promotingTo].charAt(0), startingLoc);
        ret.setLoc(piece.pieceLoc);
        return ret;
    }

    public static Piece createPieceFromFen(char c, Location pieceLocation) {
        if (!Character.isLetter(c)) MyError.error("Character was not a letter");
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
            default:
                return null;
        }
    }


    public static boolean compareMovementType(int piece1Type, int piece2Type) {
        return piece1Type == piece2Type ||
                (isDiagonalPiece(piece1Type) && isDiagonalPiece(piece2Type)) ||
                (isLinePiece(piece1Type) && isLinePiece(piece2Type));
    }

    public static boolean isDiagonalPiece(int pieceType) {
        return pieceType == BISHOP || pieceType == QUEEN;
    }

    public static boolean isLinePiece(int pieceType) {
        return pieceType == ROOK || pieceType == QUEEN;
    }

    public static ArrayList<Move> convertListOfLists(ArrayList<ArrayList<Move>> lists) {
        ArrayList<Move> ret = new ArrayList();
        //todo o(no) ↓
        lists.forEach(ret::addAll);
        return ret;
    }

    public static boolean isValidPieceType(int pieceType) {
        return existsInArr(PIECES_TYPES, pieceType);
    }

    public static boolean isMinorPiece(int pieceType) {
        return existsInArr(MINOR_PIECES, pieceType);
    }

    public static boolean isMajorPiece(int pieceType) {
        return existsInArr(MAJOR_PIECES, pieceType);
    }

    private static boolean existsInArr(int[] arr, int num) {
        return Arrays.stream(arr).anyMatch(i -> i == num);
    }

    public static double getPieceWorth(int pieceType) {
        return WORTH[pieceType];
    }

    public String getPieceFen() {
        return PIECES_FENS[pieceColor][pieceType];
    }

    public String getPieceIcon() {
        return PIECES_ICONS[pieceColor][pieceType];
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

    public void setStartingLoc(Location startingLoc) {
        this.startingLoc = startingLoc;
    }

    public int getOpponent() {
        return Player.getOpponent(pieceColor);
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
        if (isCaptured())
            return ret;
        ret = pieceMoves.getLegalMoves(board);
        return ret;
    }

    public ArrayList<Move> getPseudoMovesList() {
        ArrayList<Move> ret = new ArrayList();
        if (isCaptured()) return ret;

        //todo o(no) ↓
        generatePseudoMoves().forEach(ret::addAll);
        return ret;
    }

    public abstract ArrayList<ArrayList<Move>> generatePseudoMoves();

    public PieceMoves getPieceMoves() {

        return pieceMoves;
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
        return row >= 0 && row < Controller.ROWS && col >= 0 && col < Controller.COLS;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "pieceLoc=" + pieceLoc +
                ", pieceColor=" + pieceColor +
                ", worth=" + getWorth() +
                ", captured=" + captured +
                '}';
    }

}
