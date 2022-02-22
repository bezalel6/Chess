package ver7.SharedClasses.pieces;


import ver7.SharedClasses.PlayerColor;
import ver7.SharedClasses.StrUtils;
import ver7.SharedClasses.moves.Direction;

import java.io.Serializable;
import java.util.Arrays;

public enum PieceType implements Serializable {
    PAWN("♙", "♟", 1),
    ROOK("♖", "♜", 5),
    BISHOP("♗", "♝", 3.2),
    KNIGHT("♘", "♞", 3.1),
    QUEEN("♕", "♛", 9),
    KING("♔", "♚", 200),
    ALL_PIECES("", "", 0);

    public static final int NUM_OF_PIECE_TYPES = 6;
    public static final PieceType[] PIECE_TYPES = new PieceType[NUM_OF_PIECE_TYPES];
    public final static PieceType[] UNIQUE_MOVES_PIECE_TYPES = {ROOK, KNIGHT, BISHOP, PAWN, KING};
    public final static PieceType[] MINOR_PIECES = {BISHOP, KNIGHT};
    public final static PieceType[] MAJOR_PIECES = {QUEEN, ROOK};
    public final static PieceType[] CAN_PROMOTE_TO = {KNIGHT, ROOK, BISHOP, QUEEN};
    private final static String[] COLORLESS_PIECES_FENS = new String[NUM_OF_PIECE_TYPES];
    private static final Direction[][] ATTACKING_DIRECTIONS = new Direction[NUM_OF_PIECE_TYPES][];

    static {
        Arrays.stream(values())
                .filter(pieceType -> pieceType != ALL_PIECES)
                .forEach(pieceType -> PIECE_TYPES[pieceType.asInt()] = pieceType);
        for (PieceType pieceType : PIECE_TYPES) {
            COLORLESS_PIECES_FENS[pieceType.asInt()] = switch (pieceType) {
                case KNIGHT -> "N";
                case PAWN -> "P";
                case ROOK -> "R";
                case QUEEN -> "Q";
                case BISHOP -> "B";
                case KING -> "K";

                default -> throw new IllegalStateException("Unexpected value: " + pieceType);
            };
        }

        ATTACKING_DIRECTIONS[PAWN.asInt()] = new Direction[]
                {
                        Direction.U_R,
                        Direction.U_L
                };

        ATTACKING_DIRECTIONS[ROOK.asInt()] = new Direction[]
                {
                        Direction.U,
                        Direction.D,
                        Direction.L,
                        Direction.R
                };
        ATTACKING_DIRECTIONS[BISHOP.asInt()] = new Direction[]
                {
                        Direction.U_R,
                        Direction.U_L,
                        Direction.D_R,
                        Direction.D_L
                };
        ATTACKING_DIRECTIONS[KNIGHT.asInt()] = new Direction[]
                {
                        Direction.U_U_R,
                        Direction.U_U_L,
                        Direction.U_R_R,
                        Direction.U_L_L,

                        Direction.D_D_R,
                        Direction.D_D_L,
                        Direction.D_R_R,
                        Direction.D_L_L,

                };

        Direction[] bishop = getAttackingDirections(BISHOP), rook = getAttackingDirections(ROOK);

        ATTACKING_DIRECTIONS[QUEEN.asInt()] = new Direction[bishop.length + rook.length];

        for (int i = 0; i < ATTACKING_DIRECTIONS[QUEEN.asInt()].length; i++) {
            Direction setting;
            if (i >= bishop.length) {
                setting = rook[i - bishop.length];
            } else {
                setting = bishop[i];
            }
            ATTACKING_DIRECTIONS[QUEEN.asInt()][i] = setting;
        }

        ATTACKING_DIRECTIONS[KING.asInt()] = ATTACKING_DIRECTIONS[QUEEN.asInt()];
    }

    public final String whiteIcon;
    public final String blackIcon;
    public final double value;

    PieceType(String whiteIcon, String blackIcon, double value) {
        this.whiteIcon = whiteIcon;
        this.blackIcon = blackIcon;
        this.value = value;
    }

    public static PieceType getPieceType(int pieceType) {
        return PIECE_TYPES[pieceType];
    }

    public boolean isSlidingPiece() {
        return switch (this) {
            case PAWN, KING, KNIGHT -> false;
            default -> true;
        };
    }

    public String getWhitePieceFen() {
        return COLORLESS_PIECES_FENS[this.asInt()];
    }

    public int asInt() {
        return ordinal();
    }

    public String getPieceIcon(PlayerColor playerColor) {
        return playerColor == PlayerColor.WHITE ? whiteIcon : blackIcon;
    }

    public boolean compareMovementType(PieceType compareTo) {
        return compareMovementType(this, compareTo);
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

    public String getPieceName() {
        return StrUtils.format(name());
    }

    public boolean isAttack(Direction direction) {
        return Arrays.stream(getAttackingDirections()).anyMatch(dir -> dir == direction);
    }

    public Direction[] getAttackingDirections() {
        return getAttackingDirections(this);
    }

    public static Direction[] getAttackingDirections(PieceType pieceType) {
        return ATTACKING_DIRECTIONS[pieceType.asInt()];
    }
}
