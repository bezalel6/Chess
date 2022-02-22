package ver4.SharedClasses.pieces;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public enum PieceType implements Serializable {
    PAWN(1),
    ROOK(5),
    BISHOP(3.2),
    KNIGHT(3.1),
    QUEEN(9),
    KING(200),
    ALL_PIECES(0);


    public static final ArrayList<PieceType> PIECE_TYPES = Arrays.stream(values())
            .filter(pieceType -> pieceType != ALL_PIECES)
            .collect(Collectors.toCollection(ArrayList::new));
    public static final int NUM_OF_PIECE_TYPES = 6;

    private final static String[] COLORLESS_PIECES_FENS = new String[NUM_OF_PIECE_TYPES];

    private final static String[] COLORLESS_PIECES_ICONS = new String[NUM_OF_PIECE_TYPES];

    public static PieceType[] UNIQUE_MOVES_PIECE_TYPES = {ROOK, KNIGHT, BISHOP, PAWN, KING};
    public static PieceType[] MINOR_PIECES = {BISHOP, KNIGHT};
    public static PieceType[] MAJOR_PIECES = {QUEEN, ROOK};
    public static PieceType[] CAN_PROMOTE_TO = {KNIGHT, ROOK, BISHOP, QUEEN};

    static {
        for (PieceType pieceType : PIECE_TYPES) {
            COLORLESS_PIECES_ICONS[pieceType.asInt()] = switch (pieceType) {
                case PAWN -> "♙";
                case ROOK -> "♖";
                case BISHOP -> "♗";
                case QUEEN -> "♕";
                case KNIGHT -> "♘";
                case KING -> "♔";

                default -> throw new IllegalStateException("Unexpected value: " + pieceType);
            };
            COLORLESS_PIECES_FENS[pieceType.asInt()] = switch (pieceType) {
                case KNIGHT -> "N";
                case PAWN -> "P";
                case ROOK -> "R";
                case QUEEN -> "Q";
                case BISHOP -> "B";
                case KING -> "K";
                case ALL_PIECES -> "";
            };
        }
    }

    public final double value;

    PieceType(double value) {
        this.value = value;
    }

    public static PieceType getPieceType(int pieceType) {
        return PIECE_TYPES.get(pieceType);
    }

    public static boolean compareMovementType(PieceType piece1Type, PieceType piece2Type) {
        return piece1Type == piece2Type ||
                (isDiagonalPiece(piece1Type) && isDiagonalPiece(piece2Type)) ||
                (isLinePiece(piece1Type) && isLinePiece(piece2Type));
    }

    public static boolean isDiagonalPiece(PieceType pieceType) {
        return pieceType == PieceType.BISHOP || pieceType == PieceType.QUEEN;
    }

    public static boolean isLinePiece(PieceType pieceType) {
        return pieceType == PieceType.ROOK || pieceType == PieceType.QUEEN;
    }

    public String getWhitePieceFen() {
        return COLORLESS_PIECES_FENS[this.asInt()];
    }

    public String getPieceIcon() {
        return COLORLESS_PIECES_ICONS[this.asInt()];
    }

    public boolean compareMovementType(PieceType compareTo) {
        return compareMovementType(this, compareTo);
    }

    public String getPieceName() {
        return this.name().substring(0, 1).toUpperCase(Locale.ROOT) + this.name().substring(1).toLowerCase(Locale.ROOT);
    }

    public int asInt() {
        return ordinal();
    }

}
