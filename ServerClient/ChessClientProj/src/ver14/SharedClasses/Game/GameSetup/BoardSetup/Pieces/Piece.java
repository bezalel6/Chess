package ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces;

import ver14.SharedClasses.Game.PlayerColor;


/**
 * Piece - represents a combination of a piece type and color.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 * @see PieceType
 * @see PlayerColor
 */
public enum Piece {

    /**
     * represents a White p piece.
     */
    W_P(PieceType.PAWN, PlayerColor.WHITE),
    /**
     * represents a White Rook.
     */
    W_R(PieceType.ROOK, PlayerColor.WHITE),
    /**
     * represents a White Bishop.
     */
    W_B(PieceType.BISHOP, PlayerColor.WHITE),
    /**
     * represents a White Knight.
     */
    W_N(PieceType.KNIGHT, PlayerColor.WHITE),
    /**
     * represents a White Queen.
     */
    W_Q(PieceType.QUEEN, PlayerColor.WHITE),
    /**
     * represents a White King.
     */
    W_K(PieceType.KING, PlayerColor.WHITE),

    /**
     * represents a Black p piece.
     */
    B_P(PieceType.PAWN, PlayerColor.BLACK),
    /**
     * represents a Black Rook.
     */
    B_R(PieceType.ROOK, PlayerColor.BLACK),
    /**
     * represents a Black Bishop.
     */
    B_B(PieceType.BISHOP, PlayerColor.BLACK),
    /**
     * represents a Black Knight.
     */
    B_N(PieceType.KNIGHT, PlayerColor.BLACK),
    /**
     * represents a Black Queen.
     */
    B_Q(PieceType.QUEEN, PlayerColor.BLACK),
    /**
     * represents a Black King.
     */
    B_K(PieceType.KING, PlayerColor.BLACK);

    /**
     * The constant ALL_PIECES.
     */
    public static final Piece[] ALL_PIECES = values();
    private final static Piece[][] piecesMap = new Piece[PlayerColor.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
    private final static String[][] PIECES_ICONS = new String[PlayerColor.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];

    static {
        for (PlayerColor playerColor : PlayerColor.PLAYER_COLORS) {
            for (PieceType pieceType : PieceType.PIECE_TYPES) {
                PIECES_ICONS[playerColor.asInt][pieceType.asInt] = pieceType.getPieceIcon(playerColor);
            }
        }
        for (Piece piece : ALL_PIECES) {
            piecesMap[piece.playerColor.asInt][piece.pieceType.asInt] = piece;
        }

    }

    /**
     * The Piece type.
     */
    public final PieceType pieceType;
    /**
     * The Player color.
     */
    public final PlayerColor playerColor;

    Piece(PieceType pieceType, PlayerColor playerColor) {
        this.pieceType = pieceType;
        this.playerColor = playerColor;
    }

    /**
     * Gets piece from fen.
     *
     * @param c the c
     * @return the piece from fen
     */
    public static Piece getPieceFromFen(char c) {
        PlayerColor playerColor = Character.isUpperCase(c) ? PlayerColor.WHITE : PlayerColor.BLACK;
        PieceType pieceType = switch (Character.toLowerCase(c)) {
            case 'p' -> PieceType.PAWN;
            case 'r' -> PieceType.ROOK;
            case 'n' -> PieceType.KNIGHT;
            case 'b' -> PieceType.BISHOP;
            case 'q' -> PieceType.QUEEN;
            case 'k' -> PieceType.KING;
            default -> throw new IllegalStateException("Unexpected value: " + Character.toLowerCase(c));
        };
        return getPiece(pieceType, playerColor);
    }

    /**
     * Gets piece.
     *
     * @param pieceType   the piece type
     * @param playerColor the player color
     * @return the piece
     */
    public static Piece getPiece(PieceType pieceType, PlayerColor playerColor) {
        return piecesMap[playerColor.asInt][pieceType.asInt];
    }

    /**
     * Gets piece icon.
     *
     * @return the piece icon
     */
    public String getPieceIcon() {
        return getPieceIcon(this);
    }

    /**
     * Gets piece icon.
     *
     * @param piece the piece
     * @return the piece icon
     */
    public static String getPieceIcon(Piece piece) {
        return PIECES_ICONS[piece.playerColor.asInt][piece.pieceType.asInt];
    }

    /**
     * Gets fen.
     *
     * @return the fen
     */
    public String getFen() {
        if (playerColor == PlayerColor.WHITE)
            return pieceType.getWhitePieceFen();
        return pieceType.getWhitePieceFen().toLowerCase();
    }


    /**
     * Is on my team boolean.
     *
     * @param otherPiece the other piece
     * @return the boolean
     */
    public boolean isOnMyTeam(Piece otherPiece) {

        return otherPiece != null && otherPiece.playerColor == this.playerColor;
    }

    /**
     * Is on my team boolean.
     *
     * @param otherPlayerColor the other player color
     * @return the boolean
     */
    public boolean isOnMyTeam(PlayerColor otherPlayerColor) {
        return this.playerColor == otherPlayerColor;
    }
}