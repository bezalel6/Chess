package ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces;


import ver14.SharedClasses.Game.Moves.Direction;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Utils.ArrUtils;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Arrays;


/**
 * represents the Piece Type.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum PieceType implements Serializable {
    /**
     * Pawn Piece Type.
     */
    PAWN("♙", "♟", 100, false) {
        @Override
        public boolean isAttack(Direction direction, int maxDistance) {
            return maxDistance == 1 && super.isAttack(direction, maxDistance);
        }

        @Override
        public Direction[] getWalkingDirections() {
            return ArrUtils.concat(super.getWalkingDirections(), Direction.U, Direction.U_U);
        }
    },
    /**
     * Rook piece type.
     */
    ROOK("♖", "♜", 500),
    /**
     * Bishop piece type.
     */
    BISHOP("♗", "♝", 320),
    /**
     * Knight piece type.
     */
    KNIGHT("♘", "♞", 310, false) {
        @Override
        public boolean isAttack(Direction direction, int maxDistance) {
            return maxDistance == 2 && super.isAttack(direction, maxDistance);
        }
    },
    /**
     * Queen piece type.
     */
    QUEEN("♕", "♛", 900),
    /**
     * King piece type.
     */
    KING("♔", "♚", 200000, false) {
        @Override
        public boolean isAttack(Direction direction, int maxDistance) {
            return maxDistance == 1 && super.isAttack(direction, maxDistance);
        }
    };

    /**
     * The constant NUM_OF_PIECE_TYPES.
     */
    public static final int NUM_OF_PIECE_TYPES = 6;
    /**
     * The constant PIECE_TYPES.
     */
    public static final PieceType[] PIECE_TYPES = new PieceType[NUM_OF_PIECE_TYPES];
    /**
     * The Unique moves piece types.
     */
    public final static PieceType[] UNIQUE_MOVES_PIECE_TYPES = {ROOK, KNIGHT, BISHOP, PAWN, KING};
    /**
     * The Minor pieces.
     */
    public final static PieceType[] MINOR_PIECES = {BISHOP, KNIGHT};
    /**
     * The Major pieces.
     */
    public final static PieceType[] MAJOR_PIECES = {QUEEN, ROOK};
    /**
     * The types of pieces a pawn Can promote to.
     */
    public final static PieceType[] CAN_PROMOTE_TO = {KNIGHT, ROOK, BISHOP, QUEEN};
    /**
     * The constant ATTACKING_PIECE_TYPES.
     */
    public static final PieceType[] ATTACKING_PIECE_TYPES = {ROOK, BISHOP, KNIGHT, PAWN, QUEEN, KING};
    /**
     * The Attacking directions. each piece type has its own set of attacking direction.
     */
    static final Direction[][] ATTACKING_DIRECTIONS = new Direction[NUM_OF_PIECE_TYPES][];
    private final static String[] COLORLESS_PIECES_FENS = new String[NUM_OF_PIECE_TYPES];

    static {
        Arrays.stream(values())
                .forEach(pieceType -> PIECE_TYPES[pieceType.asInt] = pieceType);
        for (PieceType pieceType : PIECE_TYPES) {
            COLORLESS_PIECES_FENS[pieceType.asInt] = switch (pieceType) {
                case KNIGHT -> "N";
                case PAWN -> "P";
                case ROOK -> "R";
                case QUEEN -> "Q";
                case BISHOP -> "B";
                case KING -> "K";

                default -> throw new IllegalStateException("Unexpected value: " + pieceType);
            };
        }

        ATTACKING_DIRECTIONS[PAWN.asInt] = new Direction[]
                {
                        Direction.U_R,
                        Direction.U_L
                };

        ATTACKING_DIRECTIONS[ROOK.asInt] = new Direction[]
                {
                        Direction.U,
                        Direction.D,
                        Direction.L,
                        Direction.R
                };
        ATTACKING_DIRECTIONS[BISHOP.asInt] = new Direction[]
                {
                        Direction.U_R,
                        Direction.U_L,
                        Direction.D_R,
                        Direction.D_L
                };
        ATTACKING_DIRECTIONS[KNIGHT.asInt] = new Direction[]
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

        ATTACKING_DIRECTIONS[QUEEN.asInt] = new Direction[bishop.length + rook.length];

        for (int i = 0; i < ATTACKING_DIRECTIONS[QUEEN.asInt].length; i++) {
            Direction setting;
            if (i >= bishop.length) {
                setting = rook[i - bishop.length];
            } else {
                setting = bishop[i];
            }
            ATTACKING_DIRECTIONS[QUEEN.asInt][i] = setting;
        }

        ATTACKING_DIRECTIONS[KING.asInt] = ATTACKING_DIRECTIONS[QUEEN.asInt];
    }

    /**
     * The White icon.
     */
    public final String whiteIcon;
    /**
     * The Black icon.
     */
    public final String blackIcon;
    /**
     * The Value.
     */
    public final int value;
    /**
     * The Is sliding.
     */
    public final boolean isSliding;
    /**
     * The As int.
     */
    public final int asInt;

    PieceType(String whiteIcon, String blackIcon, int value) {
        this(whiteIcon, blackIcon, value, true);
    }

    PieceType(String whiteIcon, String blackIcon, int value, boolean isSliding) {
        this.whiteIcon = whiteIcon;
        this.blackIcon = blackIcon;
        this.value = value * 10;
        this.isSliding = isSliding;
        this.asInt = ordinal();

    }

    /**
     * Gets piece type.
     *
     * @param pieceType the piece type
     * @return the piece type
     */
    public static PieceType getPieceType(int pieceType) {
        return PIECE_TYPES[pieceType];
    }

    /**
     * Get attacking directions direction [ ].
     *
     * @param pieceType the piece type
     * @return the direction [ ]
     */
    public static Direction[] getAttackingDirections(PieceType pieceType) {
        return ATTACKING_DIRECTIONS[pieceType.asInt];
    }

    /**
     * Is line piece boolean.
     *
     * @param pieceType the piece type
     * @return the boolean
     */
    public static boolean isLinePiece(PieceType pieceType) {
        return pieceType == PieceType.ROOK || pieceType == PieceType.QUEEN;
    }

    /**
     * Gets white piece fen.
     *
     * @return the white piece fen
     */
    public String getWhitePieceFen() {
        return COLORLESS_PIECES_FENS[this.asInt];
    }

    /**
     * Gets piece icon.
     *
     * @param playerColor the player color
     * @return the piece icon
     */
    public String getPieceIcon(PlayerColor playerColor) {
        return playerColor == PlayerColor.WHITE ? whiteIcon : blackIcon;
    }

    /**
     * Gets piece name.
     *
     * @return the piece name
     */
    public String getPieceName() {
        return StrUtils.format(name().toLowerCase());
    }

    /**
     * Is this piece type attacking a square a {@code maxDistance} away in a {@code direction}.
     *
     * @param direction   the direction
     * @param maxDistance the max distance to the square
     * @return <code>true</code> if this piece can attack the square
     */
    public boolean isAttack(Direction direction, int maxDistance) {
        return Arrays.stream(getAttackingDirections()).anyMatch(dir -> dir == direction);
    }

    /**
     * Get attacking directions direction [ ].
     *
     * @return the direction [ ]
     */
    public Direction[] getAttackingDirections() {
        return ATTACKING_DIRECTIONS[asInt];
    }

    /**
     * Get walking directions direction [ ].
     *
     * @return the direction [ ]
     */
    public Direction[] getWalkingDirections() {
        return getAttackingDirections();
    }
}
