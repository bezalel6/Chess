package ver22_eval_captures.types;

import ver22_eval_captures.Error;
import ver22_eval_captures.Location;
import ver22_eval_captures.model_classes.Board;
import ver22_eval_captures.moves.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public abstract class Piece {
    public static final int COLS = 8, ROWS = 8;
    public static final int NUM_OF_PLAYERS = 2, NUM_OF_PIECE_TYPES = 6;
    public static final int PAWN = 0, ROOK = 1, KNIGHT = 2, BISHOP = 3, QUEEN = 4, KING = 5;

    public static final String[] PIECES_NAMES = {"Pawn", "Rook", "Knight", "Bishop", "Queen", "King"};


    public static final double[] WORTH = initWorthArr();

    public static final int[] MINOR_PIECES = {BISHOP, KNIGHT};
    public static final int[] MAJOR_PIECES = {QUEEN, ROOK};
    public static final int[] STARTING_ROW = {0, 7};
    public static final int[] CAN_PROMOTE_TO = {ROOK, KNIGHT, BISHOP, QUEEN};
    public static final int[] PIECES_TYPES = {PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING};
    public static final int[] MOST_LIKELY_TO_CHECK = {1, 2, 3, 4, 5, -1};

    private static final String[] COLORLESS_PIECES_FENS = {"P", "R", "N", "B", "Q", "K"};
    public static final String[][] PIECES_FENS = createPiecesFens();
    private static final String[] PIECES_ICONS_COLORS = {"\u001B[37m", "\u001B[30m"};
    private static final String[] COLORLESS_PIECES_ICONS = {"♙", "♖", "♘", "♗", "♕", "♔"};
    public static final String[][] PIECES_ICONS = createPiecesIcons();
    private final Location startingLoc;
    private final int pieceColor;
    private final int pieceType;

    private int difference;
    private Location pieceLoc;
    private String annotation;
    private boolean captured;

    public Piece(Location loc, int pieceColor, int pieceType, String annotation) {
        if (pieceType == PAWN && this instanceof Rook) {
            Error.error("eeeer");
        }
        this.difference = getDifference(pieceColor);
        this.pieceLoc = new Location(loc);
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.annotation = annotation;
        this.startingLoc = new Location(loc);
    }

//    public Piece(Piece other) {
//        this.difference = other.difference;
//        this.pieceLoc = new Location(other.pieceLoc);
//        this.startingLoc = new Location(other.startingLoc);
//        this.pieceColor = other.pieceColor;
//        this.pieceType = other.pieceType;
//        this.annotation = other.annotation;
//        this.captured = other.captured;
//    }

    private static String[][] createPiecesFens() {
        String[][] ret = new String[NUM_OF_PLAYERS][NUM_OF_PIECE_TYPES];
        for (int i = 0; i < ret.length; i++) {
            if (i == Player.WHITE)
                ret[i] = Arrays.copyOf(COLORLESS_PIECES_FENS, COLORLESS_PIECES_FENS.length);
            else
                Arrays.fill(ret[i], COLORLESS_PIECES_FENS[i].toLowerCase(Locale.ROOT));
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
        return player == Player.WHITE ? 1 : -1;
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
                Error.error("wrong piece type");
                return null;
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
            default:
                return null;
        }
    }

    public static boolean isInBounds(Location loc) {
        return loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS;
    }

    //    region adding move
    public static boolean addMove(Move move, int player, ArrayList<Move> list, Board board) {
        Location movingTo = move.getMovingTo();
        if (!isInBounds(movingTo))
            return false;
        Piece otherPiece = board.getPiece(movingTo);
        if (otherPiece == null) {
            list.add(move);
            return true;
        }
        if (!otherPiece.isOnMyTeam(player)) {
            move.setCapturing(otherPiece.hashCode());
            list.add(move);
        }
        return false;
    }

    /**
     * @param move
     * @param empty - the square has to be by the empty value (true - square has to be empty. false - square has to be not empty)
     * @param list
     * @param board
     * @return
     */
    public static boolean addMove(Move move, int player, boolean empty, ArrayList<Move> list, Board board) {
        Location movingTo = move.getMovingTo();
        if (!isInBounds(movingTo))
            return false;
        Piece otherPiece = board.getPiece(movingTo);
        if (otherPiece == null && empty) {
            list.add(move);
            return true;
        } else if (!empty && otherPiece != null && !otherPiece.isOnMyTeam(player)) {
            move.setCapturing(otherPiece.hashCode());
            list.add(move);
        }
        return false;
    }

    public static boolean addMove(Location movingFrom, Location movingTo, int player, ArrayList<Move> list, Board board) {
        if (isInBounds(movingTo))
            return addMove(new Move(movingFrom, movingTo, board), player, list, board);
        return false;
    }

    public static boolean addSingleMove(Location movingFrom, Location movingTo, int player, boolean empty, ArrayList<ArrayList<Move>> list, Board board) {
        return addSingleMove(new Move(movingFrom, movingTo, board), player, empty, list, board);
    }

    public static boolean addSingleMove(Move move, int player, boolean empty, ArrayList<ArrayList<Move>> list, Board board) {
        ArrayList<Move> l2 = new ArrayList<>();
        boolean ret = addMove(move, player, empty, l2, board);
        if (!l2.isEmpty())
            list.add(l2);
        return ret;
    }

    public static boolean addSingleMove(Location movingFrom, Location movingTo, int player, ArrayList<ArrayList<Move>> list, Board board) {
        return addSingleMove(new Move(movingFrom, movingTo, board), player, list, board);
    }

    public static boolean addSingleMove(Move move, int player, ArrayList<ArrayList<Move>> list, Board board) {
        ArrayList<Move> l2 = new ArrayList<>();
        boolean ret = addMove(move, player, l2, board);
        if (!l2.isEmpty())
            list.add(l2);
        return ret;
    }

    //endregion
    static boolean batchCheckBounds(Location[] list) {
        for (Location loc : list) {
            if (!isInBounds(loc))
                return false;
        }
        return true;
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

    public static int getPieceType(int hashCode) {
        //todo make the hashcode less stupid
        return (hashCode / 100) - 1;
    }

    public static boolean checkValidPieceType(int pieceType) {
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
        //todo
        int locHash = startingLoc.hashCode();
        return Integer.parseInt((pieceType + 1) + "" + locHash);
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
        public static final String[] PLAYER_NAMES = {"White", "Black"};

        public static int getOtherColor(int currentPlayer) {
            return Math.abs((currentPlayer - 1) * -1);
        }
    }

}
