package ver3.model_classes.pieces;

import ver3.model_classes.Player;

import java.util.Arrays;
import java.util.Locale;


public enum Piece {

    W_P(PieceType.PAWN, Player.WHITE),
    W_R(PieceType.ROOK, Player.WHITE),
    W_B(PieceType.BISHOP, Player.WHITE),
    W_N(PieceType.KNIGHT, Player.WHITE),
    W_Q(PieceType.QUEEN, Player.WHITE),
    W_K(PieceType.KING, Player.WHITE),

    B_P(PieceType.PAWN, Player.BLACK),
    B_R(PieceType.ROOK, Player.BLACK),
    B_B(PieceType.BISHOP, Player.BLACK),
    B_N(PieceType.KNIGHT, Player.BLACK),
    B_Q(PieceType.QUEEN, Player.BLACK),
    B_K(PieceType.KING, Player.BLACK);

    public static final Piece[] ALL_PIECES = values();
    private final static Piece[][] piecesMap = new Piece[Player.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
    private final static String[] COLORLESS_PIECES_FENS = {"P", "R", "N", "B", "Q", "K"};
    private final static String[][] PIECES_FENS = createPiecesFens();
    private final static String[] PIECES_ICONS_COLORS = {"\u001B[37m", "\u001B[30m"};
    private final static String[] COLORLESS_PIECES_ICONS = {"♙", "♖", "♘", "♗", "♕", "♔"};
    private final static String[][] PIECES_ICONS = createPiecesIcons();
    private static final int[] STARTING_ROW = {7, 0};

    static {
        for (Piece piece : ALL_PIECES) {
            piecesMap[piece.player.asInt()][piece.pieceType.asInt()] = piece;
        }
    }

    private final PieceType pieceType;
    private final Player player;

    Piece(PieceType pieceType, Player player) {
        this.pieceType = pieceType;
        this.player = player;
    }


    public static String getPieceIcon(Piece piece) {
        return PIECES_ICONS[piece.player.asInt()][piece.pieceType.asInt()];
    }

    private static String[][] createPiecesFens() {
        String[][] ret = new String[Player.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
        for (Player player : Player.PLAYERS) {
            ret[player.asInt()] = Arrays.copyOf(COLORLESS_PIECES_FENS, COLORLESS_PIECES_FENS.length);
            if (player != Player.WHITE) {
                String[] strings = ret[player.asInt()];
                for (int j = 0, stringsLength = strings.length; j < stringsLength; j++) {
                    String str = strings[j];
                    strings[j] = str.toLowerCase(Locale.ROOT);
                }
            }
        }
        return ret;
    }

    private static String[][] createPiecesIcons() {
        String[][] ret = new String[Player.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
        String reset = "\u001B[0m";
        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret[i].length; j++) {
                ret[i][j] = PIECES_ICONS_COLORS[i] + COLORLESS_PIECES_ICONS[j] + reset;
            }
        }
        return ret;
    }

    public static Piece createPieceFromFen(char c) {
        Player player = Character.isUpperCase(c) ? Player.WHITE : Player.BLACK;
        PieceType pieceType = switch (Character.toLowerCase(c)) {
            case 'p' -> PieceType.PAWN;
            case 'r' -> PieceType.ROOK;
            case 'n' -> PieceType.KNIGHT;
            case 'b' -> PieceType.BISHOP;
            case 'q' -> PieceType.QUEEN;
            case 'k' -> PieceType.KING;
            default -> throw new IllegalStateException("Unexpected value: " + Character.toLowerCase(c));
        };
        return getPiece(pieceType, player);
    }


    public static int getDifference(Player player) {
        return player == Player.WHITE ? -1 : 1;
    }

    public static Piece getPiece(PieceType pieceType, Player player) {
        return piecesMap[player.asInt()][pieceType.asInt()];
    }

    public static String getWhitePieceFen(PieceType pieceType) {
        return PIECES_FENS[Player.WHITE.asInt()][pieceType.asInt()];
    }

    public static int getStartingRow(Player player) {
        return STARTING_ROW[player.asInt()];
    }

    public int getStartingRow() {
        return getStartingRow(this.player);
    }

    public String getPieceIcon() {
        return getPieceIcon(this);
    }

    public int getDifference() {
        return getDifference(player);
    }

    public String getFen() {
        return PIECES_FENS[player.asInt()][pieceType.asInt()];
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isOnMyTeam(Player otherPlayer) {
        return this.player == otherPlayer;
    }

    public boolean isOnMyTeam(Piece otherPiece) {

        return otherPiece != null && isOnMyTeam(otherPiece.player);
    }
}