package ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces;

import ver14.SharedClasses.Game.PlayerColor;


/*
 * Piece
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * Piece -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * Piece -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public enum Piece {

    W_P(PieceType.PAWN, PlayerColor.WHITE),
    W_R(PieceType.ROOK, PlayerColor.WHITE),
    W_B(PieceType.BISHOP, PlayerColor.WHITE),
    W_N(PieceType.KNIGHT, PlayerColor.WHITE),
    W_Q(PieceType.QUEEN, PlayerColor.WHITE),
    W_K(PieceType.KING, PlayerColor.WHITE),

    B_P(PieceType.PAWN, PlayerColor.BLACK),
    B_R(PieceType.ROOK, PlayerColor.BLACK),
    B_B(PieceType.BISHOP, PlayerColor.BLACK),
    B_N(PieceType.KNIGHT, PlayerColor.BLACK),
    B_Q(PieceType.QUEEN, PlayerColor.BLACK),
    B_K(PieceType.KING, PlayerColor.BLACK);

    public static final Piece[] ALL_PIECES = values();
    private final static Piece[][] piecesMap = new Piece[PlayerColor.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
    private final static String[][] PIECES_ICONS = new String[PlayerColor.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
//    private static final int[] STARTING_ROW = {7, 0};

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

    public final PieceType pieceType;
    public final PlayerColor playerColor;

    Piece(PieceType pieceType, PlayerColor playerColor) {
        this.pieceType = pieceType;
        this.playerColor = playerColor;
    }

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

    public static Piece getPiece(PieceType pieceType, PlayerColor playerColor) {
        return piecesMap[playerColor.asInt][pieceType.asInt];
    }

    public String getPieceIcon() {
        return getPieceIcon(this);
    }

    public static String getPieceIcon(Piece piece) {
        return PIECES_ICONS[piece.playerColor.asInt][piece.pieceType.asInt];
    }

    public String getFen() {
        if (playerColor == PlayerColor.WHITE)
            return pieceType.getWhitePieceFen();
        return pieceType.getWhitePieceFen().toLowerCase();
    }


    public boolean isOnMyTeam(Piece otherPiece) {

        return otherPiece != null && otherPiece.playerColor == this.playerColor;
    }

    public boolean isOnMyTeam(PlayerColor otherPlayerColor) {
        return this.playerColor == otherPlayerColor;
    }
}