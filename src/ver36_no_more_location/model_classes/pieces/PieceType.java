package ver36_no_more_location.model_classes.pieces;


import ver36_no_more_location.Player;
import ver36_no_more_location.model_classes.Bitboard;
import ver36_no_more_location.model_classes.Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public enum PieceType {
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
    public static PieceType[] UNIQUE_MOVES_PIECE_TYPES = {ROOK, KNIGHT, BISHOP, PAWN, KING};
    public static PieceType[] MINOR_PIECES = {BISHOP, KNIGHT};
    public static PieceType[] MAJOR_PIECES = {QUEEN, ROOK};
    public static PieceType[] CAN_PROMOTE_TO = {KNIGHT, ROOK, BISHOP, QUEEN};
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

    public boolean compareMovementType(PieceType compareTo) {
        return compareMovementType(this, compareTo);
    }

    public boolean isSlidingPiece() {
        return switch (this) {
            case BISHOP, QUEEN, ROOK -> true;
            default -> false;
        };
    }

    public long getAttackingSquares(Bitboard pieceBB, Player player, Board board) {
        boolean shiftRight = player == Player.WHITE;
        return switch (this) {
            case PAWN -> pieceBB.shift(9, shiftRight)
                    .or(pieceBB.shift(7, shiftRight));
            case ROOK, BISHOP, QUEEN -> getSlidingAttack(pieceBB, player);
            case KNIGHT -> 0L;
            default -> 0L;
        };
    }

    private long getPawnsAttack(Bitboard pawnsBB, Player player) {
        return 0L;
    }

    private long getSlidingAttack(Bitboard pieceBB, Player player) {
        if (pieceBB.isEmpty()) {
            return 0L;
        }
        return 0L;
    }

    public String getPieceName() {
        return this.name().substring(0, 1).toUpperCase(Locale.ROOT) + this.name().substring(1).toLowerCase(Locale.ROOT);
    }

    public int asInt() {
        return ordinal();
    }

}
